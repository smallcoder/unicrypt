/*
 * UniCrypt
 *
 *  UniCrypt(tm) : Cryptographical framework allowing the implementation of cryptographic protocols e.g. e-voting
 *  Copyright (C) 2014 Bern University of Applied Sciences (BFH), Research Institute for
 *  Security in the Information Society (RISIS), E-Voting Group (EVG)
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniCrypt may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH), Research Institute for
 *   Security in the Information Society (RISIS), E-Voting Group (EVG)
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: unicrypt@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.unicrypt.crypto.proofgenerator.classes;

import ch.bfh.unicrypt.crypto.proofgenerator.abstracts.AbstractSigmaProofGenerator;
import ch.bfh.unicrypt.crypto.proofgenerator.challengegenerator.classes.StandardNonInteractiveSigmaChallengeGenerator;
import ch.bfh.unicrypt.crypto.proofgenerator.challengegenerator.interfaces.SigmaChallengeGenerator;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.Z;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.classes.BooleanElement;
import ch.bfh.unicrypt.math.algebra.general.classes.BooleanSet;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductGroup;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductMonoid;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductSet;
import ch.bfh.unicrypt.math.algebra.general.classes.Triple;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.function.classes.ProductFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;
import ch.bfh.unicrypt.random.interfaces.RandomByteSequence;

public class PreimageOrProofGenerator
	   extends AbstractSigmaProofGenerator<ProductSet, Pair, ProductGroup, Tuple, ProductFunction> {

	private final ProductFunction preimageProofFunction;

	protected PreimageOrProofGenerator(final SigmaChallengeGenerator challengeGenerator, final ProductFunction proofFunction) {
		super(challengeGenerator);
		this.preimageProofFunction = proofFunction;
	}

	public static PreimageOrProofGenerator getInstance(final ProductFunction proofFunction) {
		return PreimageOrProofGenerator.getInstance(proofFunction, (Element) null);
	}

	public static PreimageOrProofGenerator getInstance(final ProductFunction proofFunction, final Element proverId) {
		SigmaChallengeGenerator challengeGenerator = StandardNonInteractiveSigmaChallengeGenerator.getInstance(proofFunction, proverId);
		return PreimageOrProofGenerator.getInstance(challengeGenerator, proofFunction);
	}

	public static PreimageOrProofGenerator getInstance(final SigmaChallengeGenerator challengeGenerator, final Function... proofFunctions) {
		return PreimageOrProofGenerator.getInstance(challengeGenerator, ProductFunction.getInstance(proofFunctions));
	}

	public static PreimageOrProofGenerator getInstance(final SigmaChallengeGenerator challengeGenerator, final Function proofFunction, int arity) {
		return PreimageOrProofGenerator.getInstance(challengeGenerator, ProductFunction.getInstance(proofFunction, arity));
	}

	public static PreimageOrProofGenerator getInstance(final SigmaChallengeGenerator challengeGenerator, final ProductFunction proofFunction) {
		if (challengeGenerator == null || proofFunction == null || proofFunction.getArity() < 2) {
			throw new IllegalArgumentException();
		}
		if (PreimageOrProofGenerator.checkSpaceEquality(challengeGenerator, proofFunction)) {
			throw new IllegalArgumentException("Spaces of challenge generator and proof function are inequal.");
		}
		return new PreimageOrProofGenerator(challengeGenerator, proofFunction);
	}

	@Override
	protected ProductSet abstractGetProofSpace() {
		return ProductSet.getInstance(
			   this.getCommitmentSpace(),
			   ProductSet.getInstance(this.getChallengeSpace(), this.getPreimageProofFunction().getArity()),
			   this.getResponseSpace());
	}

	@Override
	protected ProductSet abstractGetPrivateInputSpace() {
		return ProductSet.getInstance(this.getPreimageProofFunction().getDomain(), ZMod.getInstance(this.getPreimageProofFunction().getArity()));
	}

	@Override
	protected final ProductGroup abstractGetPublicInputSpace() {
		return (ProductGroup) this.getPreimageProofFunction().getCoDomain();
	}

	@Override
	protected ProductFunction abstractGetPreimageProofFunction() {
		return this.preimageProofFunction;
	}

	public Pair createPrivateInput(Element secret, int index) {
		if (index < 0 || index >= this.getPreimageProofFunction().getArity() || !this.getPreimageProofFunction().getAt(index).getDomain().contains(secret)) {
			throw new IllegalArgumentException();
		}
		Tuple domainElements = ((ProductMonoid) this.getPreimageProofFunction().getDomain()).getIdentityElement();
		domainElements = domainElements.replaceAt(index, secret);

		return (Pair) this.getPrivateInputSpace().getElement(domainElements,
															 ZMod.getInstance(this.getPreimageProofFunction().getArity()).getElement(index));
	}

	@Override
	protected Triple abstractGenerate(Pair privateInput, Tuple publicInput, RandomByteSequence randomByteSequence) {

		// Extract secret input value and index from private input
		final int index = privateInput.getSecond().getBigInteger().intValue();
		final Element secret = ((Tuple) privateInput.getFirst()).getAt(index);

		final Function[] proofFunctions = this.getPreimageProofFunction().getAll();

		// Create lists for proof elements (t, c, s)
		final Element[] commitments = new Element[proofFunctions.length];
		final Element[] challenges = new Element[proofFunctions.length];
		final Element[] responses = new Element[proofFunctions.length];

		// Get challenge space and initialze the summation of the challenges
		final Z challengeSpace = this.getChallengeSpace();
		ZElement sumOfChallenges = challengeSpace.getIdentityElement();
		int z = challengeSpace.getOrder().intValue();
		// Create proof elements (simulate proof) for all but the known secret
		for (int i = 0; i < proofFunctions.length; i++) {
			if (i == index) {
				continue;
			}

			// Create random challenge and response
			ZElement c = challengeSpace.getRandomElement(randomByteSequence);
			Function f = proofFunctions[i];
			Element s = f.getDomain().getRandomElement(randomByteSequence);

			sumOfChallenges = sumOfChallenges.add(c);
			challenges[i] = c;
			responses[i] = s;
			// Calculate commitment based on the the public value and the random challenge and response
			// t = f(s)/(y^c)
			commitments[i] = f.apply(s).apply(((Tuple) publicInput).getAt(i).selfApply(c).invert());
		}

		// Create the proof of the known secret (normal preimage-proof, but with a special challange)
		// - Create random element and calculate commitment
		final Element randomElement = proofFunctions[index].getDomain().getRandomElement(randomByteSequence);
		commitments[index] = proofFunctions[index].apply(randomElement);

		// - Create overall proof challenge
		final ZElement challenge = this.getChallengeGenerator().generate(publicInput, Tuple.getInstance(commitments));
		// - Calculate challenge based on the overall challenge and the chosen challenges for the simulated proofs
		challenges[index] = challenge.subtract(sumOfChallenges);
		// - finally compute response element
		responses[index] = randomElement.apply(secret.selfApply(challenges[index]));

		// Return proof
		return (Triple) this.getProofSpace().getElement(Tuple.getInstance(commitments),
														Tuple.getInstance(challenges),
														Tuple.getInstance(responses));

	}

	@Override
	protected BooleanElement abstractVerify(Triple proof, Tuple publicInput) {

		// Extract (t, c, s)
		final Tuple commitments = this.getCommitment(proof);
		final Tuple challenges = (Tuple) this.getChallenge(proof);
		final Tuple responses = this.getResponse(proof);

		// 1. Check whether challenges sum up to the overall challenge
		final ZElement challenge = this.getChallengeGenerator().generate(publicInput, commitments);
		ZElement sumOfChallenges = this.getChallengeSpace().getIdentityElement();
		for (int i = 0; i < challenges.getArity(); i++) {
			sumOfChallenges = sumOfChallenges.add(challenges.getAt(i));
		}
		if (!challenge.isEquivalent(sumOfChallenges)) {
			return BooleanSet.FALSE;
		}

		// 2. Verify all subproofs
		for (int i = 0; i < this.getPreimageProofFunction().getArity(); i++) {
			Element a = this.getPreimageProofFunction().getAt(i).apply(responses.getAt(i));
			Element b = commitments.getAt(i).apply(publicInput.getAt(i).selfApply(challenges.getAt(i)));
			if (!a.isEquivalent(b)) {
				return BooleanSet.FALSE;
			}
		}

		// Proof is valid!
		return BooleanSet.TRUE;
	}

}
