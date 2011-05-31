package de.tud.stg.tigerseye.test;

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.popart.builder.test.junit.ConditionalWithNestingDSL;
import de.tud.stg.popart.builder.test.junit.SmallCombinedMapMathDSL;
import de.tud.stg.popart.builder.test.junit.TestConditionalDSL;
import de.tud.stg.popart.builder.test.junit.TestForEachSyntaxDSL;
import de.tud.stg.popart.builder.test.junit.TestMapDSL;
import de.tud.stg.popart.builder.test.junit.TestMathDSL;
import de.tud.stg.popart.builder.test.junit.TestNumberRepresentationDSL;
import de.tud.stg.popart.builder.test.junit.TestPrefixDSL;
import de.tud.stg.popart.builder.test.junit.TestSetDSL;
import de.tud.stg.popart.builder.test.junit.TestSimpleSqlDSL;
import de.tud.stg.popart.builder.test.junit.TestStatefulDSL;
import de.tud.stg.popart.builder.test.junit.TestWordMachine;

@RunWith(Suite.class)
@SuiteClasses({ConditionalWithNestingDSL.class, //
	SmallCombinedMapMathDSL.class, //
//	TestBigCombinedMapMathDSL.class, //
//	TestBnfDSL.class, //
	TestConditionalDSL.class, //
	TestForEachSyntaxDSL.class, //
	TestMapDSL.class, //
	TestMathDSL.class, //
	TestNumberRepresentationDSL.class, //
	TestPrefixDSL.class, //
	TestSetDSL.class, //
	TestSimpleSqlDSL.class, //
	TestStatefulDSL.class, //
//	TestStateMaschineDSL.class, //
	TestWordMachine.class, //
})
public class RunFast {

}