package ch.bfh.unicrypt.crypto.proofgenerator.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.bfh.unicrypt.crypto.proofgenerator.abstracts.ProofGeneratorAbstract;
import ch.bfh.unicrypt.crypto.proofgenerator.interfaces.SigmaProofGenerator;
import ch.bfh.unicrypt.math.algebra.additive.interfaces.AdditiveElement;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductGroup;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Group;
import ch.bfh.unicrypt.math.function.classes.HashFunction;
import ch.bfh.unicrypt.math.function.classes.ProductFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;

public class OrSigmaProofGeneratorClass extends ProofGeneratorAbstract implements SigmaProofGenerator {

  private SigmaProofGenerator sigmaProofGenerator;
  private final Function[] functions;
  private final ProductFunction function;
  private final HashAlgorithm hashAlgorithm;
  private final ConcatParameter concatParameter;
  private final Mapper mapper;

  public OrSigmaProofGeneratorClass(final List<Function> functions) {
    this((Function[]) functions.toArray(), SigmaProofGenerator.DEFAULT_HASH_ALGORITHM,
         SigmaProofGenerator.DEFAULT_CONCAT_ALGORITHM,
         SigmaProofGenerator.DEFAULT_MAPPER);
  }

  public OrSigmaProofGeneratorClass(final Function... functions) {
    this(functions, SigmaProofGenerator.DEFAULT_HASH_ALGORITHM,
         SigmaProofGenerator.DEFAULT_CONCAT_ALGORITHM,
         SigmaProofGenerator.DEFAULT_MAPPER);
  }

  public OrSigmaProofGeneratorClass(final Function[] functions,
          final HashAlgorithm hashAlgorithm,
          final ConcatParameter concatParameter, final Mapper mapper) {
    function = new ProductFunction(functions);

    this.hashAlgorithm = hashAlgorithm;
    this.concatParameter = concatParameter;
    this.mapper = mapper;


    this.functions = functions;
  }

  /**
   * @param secretInput
   * @param index
   * @param publicInput
   * @param otherInput
   * @param random
   * @return
   */
  public Tuple generate(final Element secretInput, final int index,
          final Element publicInput, final Element otherInput,
          final Random random) {

    if (function.getArityIn() != publicInput.getArity()) {
      throw new IllegalArgumentException("Arity of public input and proof function does not match");
    }

    int n = publicInput.getArity();
    AtomicElement secret = (AtomicElement) secretInput;

    ArrayList<Element> c = new ArrayList<Element>();
    ArrayList<Element> s = new ArrayList<Element>();
    for (int j = 0; j < n; j++) {
      c.add((AtomicElement) secret.getGroup().getRandomElement(random));
      s.add((AtomicElement) secret.getGroup().getRandomElement(random));
    }


    c.set(index, null);
    s.set(index, null);

    ArrayList<Element> t = new ArrayList<Element>();

    // compute the commitments
    for (int j = 0; j < n; j++) {
      if (j != index) {
        t.add(functions[j].apply(s.get(j)).apply(((Tuple) publicInput).getElementAt(j).selfApply((AtomicElement) c.get(j).invert())));
      } else {
        t.add(null);
      }
    }

    final Element randomElement = functions[0].getDomain().getRandomElement(random);
    t.set(index, functions[index].apply(randomElement));

    Tuple commitment = ProductGroup.createTupleElement(t);


    this.sigmaProofGenerator = new SigmaProofGenerator(functions[index], hashAlgorithm, concatParameter, mapper);
    AdditiveElement challenge = this.sigmaProofGenerator.createChallenge(commitment, publicInput, otherInput);

    // Summing up all the c values, except the one for our value we want to prove

    AdditiveElement sum_c = null;
    for (int j = 0; j < n; j++) {
      if (j != index) {
        if (sum_c == null) {
          sum_c = (AdditiveElement) c.get(j);
        } else {
          sum_c = sum_c.apply(c.get(j));
        }
      }
    }

    // subtracting the sum from the challenge
    c.set(index, challenge.apply(sum_c.invert()));

    s.set(index, (AtomicElement) randomElement.apply(c.get(index).selfApply(secret)));

    Element tElement = ((ProductGroup) this.getCommitmentSpace()).getElement(t);
    Element cElement = ((ProductGroup) this.getChallengeSpace()).getElement(c);
    Element sElement = ((ProductGroup) this.getResponseSpace()).getElement(s);

    return this.getProofSpace().getElement(tElement, cElement, sElement);
  }

  /* (non-Javadoc)
   * @see ch.bfh.unicrypt.nizkp.interfaces.ProofGenerator#verify(ch.bfh.unicrypt.math.element.interfaces.Tuple, ch.bfh.unicrypt.math.element.interfaces.Element, ch.bfh.unicrypt.math.element.interfaces.Element)
   */
  public boolean verify(final Tuple proof, final Element publicInput,
          final Element otherInput) {

    // Input validation
    if (proof == null || proof.getArity() != 3) {
      throw new IllegalArgumentException("Proof must have arity of 3");
    }

    Tuple commitment = (Tuple) getCommitment(proof);
    Tuple response = (Tuple) getResponse(proof);
    Tuple challenge = (Tuple) getChallenge(proof);

    if (commitment.getArity() != response.getArity() || response.getArity() != challenge.getArity()) {
      throw new IllegalArgumentException("Arities of commitment, challenge and response are not equal!");
    }

    Element sApplied = null;

    // applying function on all response values and check if correct
    for (int j = 0; j < response.getArity(); j++) {
      sApplied = functions[j].apply(response.getElementAt(j));
      if (!sApplied.getSet().areEqual(sApplied, commitment.getElementAt(j).apply(((Tuple) publicInput).getElementAt(j).selfApply((AtomicElement) challenge.getElementAt(j))))) {
        return false;
      }
    }

    this.sigmaProofGenerator = new SigmaProofGenerator(functions[0], hashAlgorithm, concatParameter, mapper);
    AdditiveElement verificationChallenge = this.sigmaProofGenerator.createChallenge(commitment, publicInput, otherInput);

    AdditiveElement sum_c = null;
    for (int j = 0; j < response.getArity(); j++) {
      if (sum_c == null) {
        sum_c = (AdditiveElement) challenge.getElementAt(j);
      } else {
        sum_c = sum_c.apply(challenge.getElementAt(j));
      }
    }

    return sum_c.getGroup().areEqual(sum_c, verificationChallenge);
  }

  @Override
  public ProductGroup getProofSpace() {
    return new ProductGroup(getCommitmentSpace(), getChallengeSpace(), getResponseSpace());
  }

  @Override
  public ProductFunction getProofFunction() {
    return this.function;
  }

  @Override
  public ProductGroup getCoDomain() {
    return (ProductGroup) super.getCoDomain();
  }

  @Override
  public Group getCommitmentSpace() {
    return this.function.getCoDomain();
  }

  public Group getChallengeSpace() {
    return this.function.getDomain();
  }

  @Override
  public Group getResponseSpace() {
    return this.function.getDomain();
  }

  @Override
  public Element getCommitment(Tuple proof) {
    return proof.getElementAt(0);
  }

  @Override
  public Element getResponse(Tuple proof) {
    return proof.getElementAt(2);
  }

  public Element getChallenge(Tuple proof) {
    return proof.getElementAt(1);
  }

  @Override
  public HashFunction getHashFunction() {
    return this.sigmaProofGenerator.getHashFunction();
  }

  @Override
  public Tuple generate(Element secretInput, Element publicInput,
          Element otherInput, Random random) {
    throw new UnsupportedOperationException("Please use the generate method with index.");
  }

}