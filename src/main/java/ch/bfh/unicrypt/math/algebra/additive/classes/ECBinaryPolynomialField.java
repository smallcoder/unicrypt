package ch.bfh.unicrypt.math.algebra.additive.classes;

import ch.bfh.unicrypt.math.algebra.additive.abstracts.AbstractEC;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.BinaryPolynomialElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.BinaryPolynomialField;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.utility.MathUtil;
import java.math.BigInteger;
import java.util.Random;

public class ECBinaryPolynomialField
			 extends AbstractEC<ECBinaryPolynomialFieldElement, BinaryPolynomialField, BinaryPolynomialElement> {

	public ECBinaryPolynomialField(BinaryPolynomialField finiteField, BinaryPolynomialElement a,
				 BinaryPolynomialElement b, BinaryPolynomialElement gx, BinaryPolynomialElement gy,
				 BigInteger order, BigInteger h) {
		super(finiteField, a, b, gx, gy, order, h);
	}

	public ECBinaryPolynomialField(BinaryPolynomialField finiteField, BinaryPolynomialElement a,
				 BinaryPolynomialElement b,
				 BigInteger order, BigInteger h) {
		super(finiteField, a, b, order, h);
	}

	@Override
	protected ECBinaryPolynomialFieldElement abstractApply(Element element1, Element element2) {
		BinaryPolynomialElement s, rx, ry, px, py, qx, qy;
		ECBinaryPolynomialFieldElement p = (ECBinaryPolynomialFieldElement) element1;
		ECBinaryPolynomialFieldElement q = (ECBinaryPolynomialFieldElement) element2;
		px = p.getX();
		py = p.getY();
		qx = q.getX();
		qy = q.getY();

		if (p.isZero()) {
			return q;
		} else {
			if (q.isZero()) {
				return p;
			} else {
				if (p.equals(q.invert())) {
					return this.getIdentityElement();
				} else {
					if (element1.equals(element2)) {
						final DualisticElement one = this.getFiniteField().getElement(1);
						s = px.add(py.divide(px));
						rx = s.power(2).add(s).add(this.getA());
						ry = px.power(2).add((s.add(one)).multiply(rx));
						return this.getElement(rx, ry);
					} else {
						DualisticElement two = this.getFiniteField().getElement(2);
						s = py.add(qy).divide(px.add(qx));
						rx = s.power(2).add(s).add(px).add(qx).add(this.getA());
						ry = s.multiply(px.add(rx)).add(rx).add(py);
						return this.getElement(rx, ry);
					}
				}
			}
		}

	}

	@Override
	protected ECBinaryPolynomialFieldElement abstractInvert(Element element) {
		ECBinaryPolynomialFieldElement e = (ECBinaryPolynomialFieldElement) element;
		return getElement(e.getX(), e.getX().add(e.getY()));
	}

	@Override
	protected ECBinaryPolynomialFieldElement getRandomElementWithoutGenerator(Random random) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Boolean contains(BinaryPolynomialElement x, BinaryPolynomialElement y) {
		BinaryPolynomialElement left = y.power(2).add(x.multiply(getA()));
		BinaryPolynomialElement right = x.power(3).add(x.power(2).multiply(getA())).add(getB());
		return left.equals(right);
	}

	@Override
	protected boolean isValid() {
		BigInteger m = new BigInteger("131"); //Must be set correctly
		final BigInteger TWO = new BigInteger("2");
		boolean c1, c2, c3, c4, c5, c6, c7, c81, c82;
		c1 = true; //-> Must be added!!
		c2 = !this.getB().equals(this.getFiniteField().getZeroElement());
		c3 = contains(this.getDefaultGenerator());
		c4 = MathUtil.arePrime(this.getOrder());
		c5 = 0 >= this.getH().compareTo(new BigInteger("4"));

		c6 = this.getDefaultGenerator().selfApply(getOrder()).isZero();
		c7 = this.getH().equals(MathUtil.sqrt(TWO.pow(m.intValue())).add(BigInteger.ONE).pow(2).divide(getOrder()));

		c81 = true;
		c82 = true;
		for (int i = 1; i < 20; i++) {
			BigInteger b = new BigInteger(String.valueOf(i));
			if (TWO.modPow(m.multiply(b), getOrder()).equals(BigInteger.ONE)) {
				c81 = false;
			}

			if (getOrder().multiply(getH()).equals(TWO.pow(m.intValue()))) {
				c82 = false;
			}
		}

		return c1 && c2 && c3 && c4 && c5 && c6 && c7 && c81 && c82;
	}

	/**
	 * Returns an elliptic curve over Fp y²=x³+ax+b
	 * <p>
	 * @param f     Finite field of type ZModPrime
	 * @param a     Element of Fp respresnting a in the curve equation
	 * @param b     Element of Fp respresnting b in the curve equation
	 * @param order Order of the the used subgroup
	 * @param h     Co-factor h*order= N -> total order of the group
	 * @return
	 */
	public static ECBinaryPolynomialField getInstance(BinaryPolynomialField f, BinaryPolynomialElement a, BinaryPolynomialElement b, BigInteger order, BigInteger h) {
		return new ECBinaryPolynomialField(f, a, b, order, h);
	}

	/**
	 * Returns an elliptic curve over Fp y²=x³+ax+b
	 * <p>
	 * @param f     Finite field of type ZModPrime
	 * @param a     Element of Fp respresnting a in the curve equation
	 * @param b     Element of Fp respresnting b in the curve equation
	 * @param gx    x-coordinate of the generator
	 * @param gy    y-coordinate of the generator
	 * @param order Order of the the used subgroup
	 * @param h     Co-factor h*order= N -> total order of the group
	 * @return
	 */
	public static ECBinaryPolynomialField getInstance(BinaryPolynomialField f, BinaryPolynomialElement a, BinaryPolynomialElement b, BinaryPolynomialElement gx, BinaryPolynomialElement gy, BigInteger order, BigInteger h) {
		return new ECBinaryPolynomialField(f, a, b, gx, gy, order, h);
	}

	@Override
	protected ECBinaryPolynomialFieldElement abstractGetElement(
				 BinaryPolynomialElement x, BinaryPolynomialElement y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ECBinaryPolynomialFieldElement abstractGetIdentityElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Boolean contains(BinaryPolynomialElement x) {
		// TODO Auto-generated method stub
		return null;
	}

}