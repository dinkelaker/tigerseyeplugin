package de.tud.stg.popart.builder.test.junit;

import static de.tud.stg.popart.builder.test.TestUtils.test;

import org.junit.Test;

import de.tud.stg.popart.builder.test.dsls.MathDSL;

public class TestMathDSL {
	@Test
	public void testMathDSL() {
		test("MathDSL", MathDSL.class);
	}
}
