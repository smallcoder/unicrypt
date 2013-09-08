/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.unicrypt.math.function.classes;

import ch.bfh.unicrypt.math.algebra.additive.classes.ZPlusMod;
import ch.bfh.unicrypt.math.algebra.general.interfaces.CyclicGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.function.abstracts.AbstractFunction;
import java.util.Random;

/**
 *
 * @author rolfhaenni
 */
public class GeneratorFunction extends AbstractFunction<ZPlusMod, CyclicGroup, Element> {

  Element generator;

  public GeneratorFunction(ZPlusMod domain, CyclicGroup coDomain, Element generator) {
    super(domain, coDomain);
    this.generator = generator;
  }

  @Override
  protected Element abstractApply(Element element, Random random) {
    return generator.selfApply(element);
  }

  public static GeneratorFunction getInstance(Element generator) {
    if (generator == null || !generator.getSet().isCyclicGroup() || !generator.isGenerator()) {
      throw new IllegalArgumentException();
    }
    CyclicGroup cyclicGroup = (CyclicGroup) generator.getSet();
    return new GeneratorFunction(cyclicGroup.getZPlusModOrder(), cyclicGroup, generator);
  }

  public static GeneratorFunction getInstance(CyclicGroup cyclicGroup) {
    if (cyclicGroup == null) {
      throw new IllegalArgumentException();
    }
    return new GeneratorFunction(cyclicGroup.getZPlusModOrder(), cyclicGroup, cyclicGroup.getDefaultGenerator());
  }

}