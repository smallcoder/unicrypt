package ch.bfh.unicrypt.math.algebra.additive.classes;

import java.math.BigInteger;
import java.util.Random;
import ch.bfh.unicrypt.math.algebra.additive.abstracts.AbstractAdditiveCyclicGroup;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.FiniteField;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.utility.MathUtil;

public abstract class ECGroup extends
		AbstractAdditiveCyclicGroup<ECGroupElement> {
	private FiniteField finiteField;
	private ECGroupElement generator;
	private DualisticElement a, b;
	private BigInteger order, h;
	private final ECGroupElement Identity;
	private final DualisticElement zero = null;

	protected ECGroup(FiniteField Finitefiled, DualisticElement a,
			DualisticElement b, DualisticElement gx, DualisticElement gy,
			BigInteger order, BigInteger h) {
		super();
		this.a = a;
		this.b = b;
		this.order = order;
		this.h = h;
		this.finiteField = Finitefiled;
		this.Identity = this.getElement(zero, zero);
		this.generator = this.getElement(gx,gy);
		
		if(!isValid()){
			throw new IllegalArgumentException("Curve parameters are not valid");
		}

	}
	
	protected ECGroup(FiniteField Finitefiled, DualisticElement a,
			DualisticElement b,	BigInteger order, BigInteger h) {
		super();
		this.a = a;
		this.b = b;
		this.order = order;
		this.h = h;
		this.finiteField = Finitefiled;
		this.Identity = this.getElement(zero, zero);
		this.generator = this.computeGenerator();
		
		if(!isValid()){
			throw new IllegalArgumentException("Curve parameters are not valid");
		}
	}


	@Override
	protected ECGroupElement abstractGetDefaultGenerator() {
			return this.generator;
	}

	protected ECGroupElement computeGenerator(){
		ECGroupElement e=this.getRandomElement().selfApply(this.getH());
		while(!this.isGenerator(e)){
			e=this.getRandomElement();
		}
		return e;
	}

	@Override
	protected boolean abstractIsGenerator(Element element) {
		ECGroupElement e = (ECGroupElement) element;
		e = e.selfApply(this.getOrder());
		return MathUtil.isPrime(this.getOrder()) && e.isZero();
	}

	@Override
	protected abstract ECGroupElement abstractInvert(Element element);

	@Override
	protected ECGroupElement abstractGetIdentityElement() {
		if(Identity==null){
			return new ECGroupElement(this, zero, zero);
		}
		return this.Identity;
	}


	@Override
	protected BigInteger abstractGetOrder() {
		return this.order;
	}

	@Override
	protected ECGroupElement abstractGetElement(BigInteger value) {
		if (value.equals(zero)) {
			return this.getIdentityElement();
		} else {
			BigInteger[] result = MathUtil.elegantUnpair(value);
			DualisticElement x = this.getFiniteField().getElement(result[0]);
			DualisticElement y = this.getFiniteField().getElement(result[1]);

<<<<<<< HEAD
			if (y.power(2).isEqual(x.power(3).add(x.multiply(a)).add(b))) {
				return new ECGroupElement(this, x, y);
=======
			if (contains(x,y)) {
				return this.getElement(x, y);
>>>>>>> ff540659e2a33e7fb65ef947f1577a26cf6d8a49
			} else {
				throw new IllegalArgumentException("(" + x + "," + y
						+ ") is not a point on the elliptic curve");
			}
		}
	}

	public ECGroupElement getElement(DualisticElement x, DualisticElement y) {
		if (x == zero && y == zero) {
			return this.getIdentityElement();
		} else if (x == zero || y == zero) {
			throw new IllegalArgumentException("One coordinate is zero");
		} else if(contains(x,y)) {
			return new ECGroupElement(this, x, y);
		}
		else{
			throw new IllegalArgumentException("Point is not an element of the curve"+ this.toString());
		}
	}

	@Override
	protected ECGroupElement abstractGetRandomElement(Random random) {
		if (this.getDefaultGenerator() != null) {
			DualisticElement r = this.getFiniteField().getRandomElement(random);
			return this.getDefaultGenerator().selfApply(r);
		} else {
			
			return this.getRandomElementWithoutGenerator(random);
						
		}
	
	}

	/**
	 * Returns random element without knowing a generator of the group
	 * @param random
	 * @return
	 */
	protected abstract ECGroupElement getRandomElementWithoutGenerator(Random random);

	@Override
	protected boolean abstractContains(BigInteger value) {
		BigInteger[] result = MathUtil.elegantUnpair(value);
		DualisticElement x = this.getFiniteField().getElement(result[0]);
		DualisticElement y = this.getFiniteField().getElement(result[1]);
<<<<<<< HEAD
		return y.power(2).isEqual(
				x.power(3).add(x.multiply(this.getA())).add(this.b));
=======
		return this.contains(x, y);
>>>>>>> ff540659e2a33e7fb65ef947f1577a26cf6d8a49
	}
	
	/*
	 * --- Abstract methods - must be implemented in concrete classes ---
	 */

	protected abstract Boolean contains(DualisticElement x, DualisticElement y);
	
	protected abstract boolean isValid();

	/*
	 * --- Getter methods for additional fields ---
	 */
	
	protected FiniteField getFiniteField() {
		return finiteField;
	}

	protected DualisticElement getB() {
		return b;
	}

	protected DualisticElement getA() {
		return a;
	}

<<<<<<< HEAD
		return y.isEqual(x);
=======
	protected BigInteger getH() {
		return h;
>>>>>>> ff540659e2a33e7fb65ef947f1577a26cf6d8a49
	}

	
	@Override
	public boolean isZeroElement(Element element) {
		ECGroupElement e = (ECGroupElement) element;
		return e.getX() == null && e.getY() == null;
	}
	
	


	
	

}