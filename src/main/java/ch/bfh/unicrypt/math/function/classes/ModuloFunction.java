package ch.bfh.unicrypt.math.function.classes;

import ch.bfh.unicrypt.math.element.Element;
import ch.bfh.unicrypt.math.function.abstracts.AbstractFunction;
import ch.bfh.unicrypt.math.group.classes.ZPlusMod;
import ch.bfh.unicrypt.math.group.interfaces.Group;
import java.math.BigInteger;
import java.util.Random;

/**
 * This class represents the concept of an identity function f:X->Z_2 with f(x)=x mod N for
 * all elements x in X.
 *
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 1.0
 */
public class ModuloFunction extends AbstractFunction {

  private BigInteger modulus;

  private ModuloFunction(final Group domain, final Group coDomain, final BigInteger modulus) {
    super(domain, coDomain);
    this.modulus = modulus;
  }

  public BigInteger getModulus() {
    return this.modulus;
  }

  //
  // The following protected method implements the abstract method from {@code AbstractFunction}
  //

  @Override
  protected Element abstractApply(final Element element, final Random random) {
    return this.getCoDomain().getElement(element.getValue().mod(this.getModulus()));
  }

  //
  // STATIC FACTORY METHODS
  //

  /**
   * This is the standard constructor for this class. It creates an identity function for a given group.
   * @param domain The given Group
   * @throws IllegalArgumentException if the group is null
   */
  public static ModuloFunction getInstance(final Group domain, BigInteger modulus) {
    return new ModuloFunction(domain, ZPlusMod.getInstance(modulus), modulus);
  }

}
