package ch.bfh.unicrypt.crypto.proofgenerator;

import ch.bfh.unicrypt.crypto.proofgenerator.classes.PreimageProofGenerator;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.general.classes.BooleanElement;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.CyclicGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModSafePrime;
import ch.bfh.unicrypt.math.function.classes.GeneratorFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;
import ch.bfh.unicrypt.math.helper.Alphabet;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PreimageProofGeneratorTest {

	final static int P = 167;
	final private CyclicGroup G_q;

	public PreimageProofGeneratorTest() {
		this.G_q = GStarModSafePrime.getInstance(P);
	}

	@Test
	public void TestPreimageProof() {

		// Proof generator
		Function f = GeneratorFunction.getInstance(this.G_q.getElement(4));
		PreimageProofGenerator pg = PreimageProofGenerator.getInstance(f);

		// Valid proof
		Element privateInput = f.getDomain().getElement(3);
		Element publicInput = f.getCoDomain().getElement(64);
		StringElement proverId = StringMonoid.getInstance(Alphabet.BASE64).getElement("Prover1");

		Tuple proof = pg.generate(privateInput, publicInput, proverId);

		BooleanElement v = pg.verify(proof, publicInput, proverId);
		assertTrue(v.getBoolean());

		// Invalid proof -> wrong private value
		privateInput = f.getDomain().getElement(4);
		proof = pg.generate(privateInput, publicInput, proverId);
		v = pg.verify(proof, publicInput, proverId);
		assertTrue(!v.getBoolean());
	}

}
