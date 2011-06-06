package de.tud.stg.tigerseye.examples.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.tud.stg.tigerseye.examples.test.logo.LogoDSLTest;
import de.tud.stg.tigerseye.examples.test.mapdsl.MapDSLTest;
import de.tud.stg.tigerseye.examples.test.setdsl.SetDSLSemanticsTest;
import de.tud.stg.tigerseye.examples.test.setdsl.SetDSLTest;
import de.tud.stg.tigerseye.examples.test.simplesql.SimpleSqlDSLTest;
import de.tud.stg.tigerseye.examples.test.unitsdsl.UnitsDSLSemanticTest;
import de.tud.stg.tigerseye.examples.test.unitsdsl.UnitsDSLTest;


@RunWith(Suite.class)
@SuiteClasses({
	LogoDSLTest.class, //
	MapDSLTest.class, //
	LogoDSLTest.class, //
	UnitsDSLTest.class, //
	UnitsDSLSemanticTest.class, //
	SimpleSqlDSLTest.class, //
	SetDSLSemanticsTest.class, //
	SetDSLTest.class, //
})
public class AllTestsExampleDSLs {

}