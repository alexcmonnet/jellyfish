/**
 * UNCLASSIFIED
 *
 * Copyright 2020 Northrop Grumman Systems Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ngc.seaside.systemdescriptor.tests.part

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.util.ResourceHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import com.ngc.seaside.systemdescriptor.tests.SystemDescriptorInjectorProvider
import com.ngc.seaside.systemdescriptor.tests.resources.Models
import com.ngc.seaside.systemdescriptor.tests.resources.Datas
import com.ngc.seaside.systemdescriptor.systemDescriptor.BasePartDeclaration

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class PartParsingTest {

	@Inject
	ParseHelper<Package> parseHelper

	@Inject
	ResourceHelper resourceHelper

	@Inject
	ValidationTestHelper validationTester

	Resource requiredResources

	@Before
	def void setup() {
		requiredResources = Models.allOf(
			resourceHelper,
			Models.ALARM,
			Models.CLOCK,
			Datas.ZONED_TIME,
			Datas.TIME_ZONE,
			Datas.DATE_TIME,
			Datas.DATE,
			Datas.TIME
		)
		validationTester.assertNoIssues(requiredResources)
	}

	@Test
	def void testDoesParseModelWithParts() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Alarm
			
			model BigClock {
				
				parts {
					Alarm alarm
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		val model = result.element as Model
		val parts = model.parts
		assertNotNull(
			"did not parse parts",
			parts
		)

		val part = model.parts.declarations.get(0)
		assertEquals(
			"part name not correct",
			"alarm",
			part.name
		)
		if (part.eClass().equals(SystemDescriptorPackage.Literals.BASE_PART_DECLARATION)) {
			assertEquals(
				"part type not correct!",
				"Alarm",
				(part as BasePartDeclaration).type.name
			)
		}
	}

	@Test
	def void testDoesParseModelWithRefinedParts() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Clock
			import clocks.datatypes.TimeZone
			
			model BigClock refines Clock{
				
				parts {
					refine emptyModel
				}
				
				properties {
					releaseDate.dataTime.date.year = 1
					releaseDate.dataTime.date.month = 1
					releaseDate.dataTime.date.day = 1
					releaseDate.timeZone = TimeZone.CST
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

	}
	
	@Test
	def void testDoesNotParseModelWithRefinedPartsWithTypeDelcared() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Clock
			
			model BigClock refines Clock{
				
				parts {
					refine AnEmptyModel emptyModel 
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PART_DECLARATION,
			null
		)

	}

	@Test
	def void testDoesNotParseModelWithPartOfTypeData() {
		val source = '''
			package clocks.models
			
			import clocks.datatypes.Time
			
			model BigClock {
				
				parts {
					Time time
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PART_DECLARATION,
			Diagnostic.LINKING_DIAGNOSTIC
		)
	}

	@Test
	def void testDoesNotParseModelWithDuplicateParts() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Alarm
			
			model BigClock {
				
				parts {
					Alarm alarm
					Alarm alarm
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PART_DECLARATION,
			null
		)
	}

	@Test
	def void testDoesNotParseModelWithDuplicatePartAndInput() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Alarm
			import clocks.datatypes.Time
			
			model BigClock {
				
				input {
					Time alarm
				}
				
				parts {
					Alarm alarm
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PART_DECLARATION,
			null
		)
	}

	@Test
	def void testDoesNotParseModelWithDuplicatePartAndOutput() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Alarm
			import clocks.datatypes.Time
			
			model BigClock {
				
				output {
					Time alarm
				}
				
				parts {
					Alarm alarm
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PART_DECLARATION,
			null
		)
	}

	@Test
	def void testDoesNotParseModelWithEscapedPartsFieldName() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Alarm
			
			model BigClock {
				
				parts {
					Alarm ^int
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PART_DECLARATION,
			null
		)
	}

	@Test
	def void testDoesNotParseANonRefinedModelThatRefinedAPart() {
		val source = '''
			package clocks.models
			
			model BigClock {
			
				parts {
					refine emptyModel {
						metadata {
									"name" : "My Part",
									"description" : "EmptyModel Part",
									"stereotypes" : ["part", "example"]
								}
					}
				}
			}
		     '''

		var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)

		validationTester.assertError(invalidResult, SystemDescriptorPackage.Literals.PART_DECLARATION, null)
	}

	@Test
	def void testDoesNotParseRefinedModelOfAPartThatWasntInTheRefinedModel() {
		val source = '''
			package clocks.models
			
			import clocks.models.part.Alarm
			
			model BigClock refines Alarm{
			
				parts {
					refine superModel
					
				}
			}
		     '''

		var invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)

		validationTester.assertError(invalidResult, SystemDescriptorPackage.Literals.PART_DECLARATION, null)
	}
}
