package com.ngc.seaside.systemdescriptor.tests

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data
import com.ngc.seaside.systemdescriptor.systemDescriptor.DataType
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import org.eclipse.emf.common.util.URI
import org.eclipse.xtext.diagnostics.Diagnostic
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.util.ResourceHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class DataParsingTest {

	@Inject
	ParseHelper<Package> parseHelper

	@Inject
	ResourceHelper resourceHelper

	@Inject
	ValidationTestHelper validationTester

	@Test
	def void testDoesParseEmptyData() {
		val source = '''
			package clocks.datatypes
			
			data Time {
			}
		'''

		val result = parseHelper.parse(source)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		assertEquals(
			"package name not correct",
			"clocks.datatypes",
			result.name
		)

		var data = result.element
		assertTrue(
			"did not parse data!",
			data instanceof Data
		)
	}

	@Test
	def void testDoesParseData() {
		val source = '''
			package clocks.datatypes
			
			data Time {
				int hour
				int minute
				int second
			}
		''';

		val result = parseHelper.parse(source)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		var data = result.element as Data
		assertEquals(
			"wrong number of fields!",
			3,
			data.fields.size
		)

		var field = data.fields.get(0)
		assertEquals(
			"data type not correct!",
			DataType.INT,
			field.type
		)
		assertEquals(
			"field name not correct!",
			"hour",
			field.name
		)
	}

	@Test
	def void testDoesParseDataWithMetadataValidation() {
		val source = '''
			package clocks.datatypes
			
			data Time {
				metadata {
				    "name": "Time",
				    "description": "Represents a local time (does not account for timezones)."
				}
				
				int hour
				int minute
				int second
			}
		''';

		val result = parseHelper.parse(source)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		var metadata = result.element.metadata
		assertEquals(
			"metadata did not parse!",
			"name",
			metadata.json.firstObject.element
		)
		assertEquals(
			"metadata did not parse!",
			"Time",
			metadata.json.firstObject.content.value
		)
	}

	@Test
	def void testDoesParseDataWithValidation() {
		val source = '''
			package clocks.datatypes
			
			data Time {
				metadata {
				    "name": "Time",
				    "description": "Represents a local time (does not account for timezones)."
				}
				
				int hour {
					"validation": {
						"min": "0",
						"max": "23"
					}
				}
				
				int minute {
					"validation": {
						"min": "0",
						"max": "59"
					}
				}
					
				int second {
					"validation": {
						"min": "0",
						"max": "59"
					}
				}
			}
		''';

		val result = parseHelper.parse(source)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		var data = result.element as Data
		var field = data.fields.get(0)
		var metadata = field.metadata
		var validation = metadata.firstObject

		assertEquals(
			"validation did not parse!",
			"validation",
			validation.element
		)

	// TODO TH: the JSON is not correct the type ObjectValue only has a string field, it is not recursive.
	// consider https://gist.github.com/nightscape/629651
	}

	@Test
	def void testDoesNotParseMultipleDataElements() {
		val source = '''
			package clocks.datatypes
			
			data Time {
			}
			
			data LocalTime {
			}
		'''

		val invalidResult = parseHelper.parse(source)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PACKAGE,
			Diagnostic.SYNTAX_DIAGNOSTIC
		)
	}

	@Test
	@Ignore("Does not check for duplicate data declarations in the same package yet")
	def void testDoesRequireUnqiueNames() {
		val dataResource = resourceHelper.resource(
			'''
				package clocks.datatypes
							
				data Time {
				}
			''',
			URI.createURI("datatypes.sd")
		)
		validationTester.assertNoIssues(dataResource)

		val source = '''
			package clocks.datatypes
			
			data Time {
			}
		'''

		val invalidResult = parseHelper.parse(source, dataResource.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.DATA,
			null
		)
	}
}
