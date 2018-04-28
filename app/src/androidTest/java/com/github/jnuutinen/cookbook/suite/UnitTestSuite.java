package com.github.jnuutinen.cookbook.suite;

import com.github.jnuutinen.cookbook.unit.CategoryEntityTest;
import com.github.jnuutinen.cookbook.unit.RecipeEntityTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CategoryEntityTest.class, RecipeEntityTest.class})
public class UnitTestSuite {
}
