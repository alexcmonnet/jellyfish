package com.ngc.seaside.systemdescriptor.tests.properties

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import com.ngc.seaside.systemdescriptor.systemDescriptor.PropertyValueAssignment
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import com.ngc.seaside.systemdescriptor.tests.SystemDescriptorInjectorProvider
import com.ngc.seaside.systemdescriptor.tests.resources.Datas
import com.ngc.seaside.systemdescriptor.tests.resources.Models
import org.eclipse.emf.ecore.EAttribute
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.util.ResourceHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import org.junit.Ignore
import org.eclipse.xtext.diagnostics.Diagnostic
import com.ngc.seaside.systemdescriptor.systemDescriptor.PropertyValue
import com.ngc.seaside.systemdescriptor.systemDescriptor.EnumPropertyValue

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class ModelPropertyValuesParsingTest {

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
			Datas.DATE,
			Datas.TIME,
			Datas.TIME_ZONE,
			Datas.TIME_CONVENTION
		)
		validationTester.assertNoIssues(requiredResources)
	}

	@Test
	def void testDoesParseModelWithPrimitivePropertyValues() {
		val source = '''
			package clocks.models
			
			model BigClock {
				properties {
					int intField
					float floatField
					boolean booleanField
					string stringField
					
					intField = 1
					floatField = 0.95
					booleanField = true
					stringField = "myString"
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		val model = result.element as Model
		val properties = model.properties
		assertNotNull(
			"did not parse properties",
			properties
		)

		var property = properties.assignments.get(0)
		assertPropertyValue(property, "intField", SystemDescriptorPackage.Literals.INT_VALUE__VALUE, 1)

		property = properties.assignments.get(1)
		assertPropertyValue(property, "floatField", SystemDescriptorPackage.Literals.DBL_VALUE__VALUE, 0.95)

		property = properties.assignments.get(2)
		assertPropertyValue(property, "booleanField", SystemDescriptorPackage.Literals.BOOLEAN_VALUE__VALUE, "true")

		property = properties.assignments.get(3)
		assertPropertyValue(property, "stringField", SystemDescriptorPackage.Literals.STRING_VALUE__VALUE, "myString")
	}

	@Test
	def void testDoesParseModelWithEnumPropertyValues() {
		val source = '''
			package clocks.models
			
			import clocks.datatypes.TimeZone
			
			model BigClock {
				properties {
					TimeZone userTimeZone
					
					userTimeZone = TimeZone.CST
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		val model = result.element as Model
		val properties = model.properties
		assertNotNull(
			"did not parse properties",
			properties
		)

		var property = properties.assignments.get(0)
		assertPropertyEnumValue(property, "userTimeZone", "TimeZone", "CST")
	}

	@Test
	@Ignore
	def void testDoesParseModelWithComplexDataTypePropertyValues() {
		fail("not yet implemented");
	}

	@Test
	@Ignore
	def void testDoesParseModelWithPropertiesFromRefinedModel() {
		fail("not yet implemented");
	}

	@Test
	def void testDoesNotParseModelIfPropertyNotDeclared() {
		val source = '''
			package clocks.models
			
			model BigClock {
				properties {
					int intField
					
					fooField = 1
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PROPERTY_VALUE_ASSIGNMENT,
			Diagnostic.LINKING_DIAGNOSTIC
		)
	}

	@Test
	@Ignore
	def void testDoesNotParseModelIfPrimitivePropertyValueTypesNotCorrect() {
		fail("not yet implemented");
	}

	@Test
	@Ignore
	def void testDoesNotParseModelIfEnumPropertyValueIsNotAnEnumConstant() {
		fail("not yet implemented");
	}

	@Test
	@Ignore
	def void testDoesNotParseModelIfComplexDataTypePropertyValueTypeIsNotCorrect() {
		fail("not yet implemented");
	}

	@Test
	@Ignore
	def void testDoesNotParseModelIfComplexDataTypePathIsNotCorrect() {
		fail("not yet implemented");
	}

	def private static void assertPropertyValue(PropertyValueAssignment property, String name, EAttribute attribute,
		Object expected) {
		assertEquals(
			"property name not correct!",
			property.declaration.name,
			name
		)
		val value = property.value;
		assertTrue(
			"property type not correct!",
			value.eClass.isSuperTypeOf(attribute.EContainingClass)
		)
		assertEquals(
			"property value not correct!",
			expected,
			value.eGet(attribute)
		)
	}

	def private static void assertPropertyEnumValue(PropertyValueAssignment property, String name,
		String enumerationTypeName, String expected) {
		val value = property.value
		assertTrue(
			"value is not a enum property value!",
			value instanceof EnumPropertyValue
		)
		assertEquals(
			"property name not correct!",
			name,
			property.declaration.name
		)
		assertEquals(
			"enumeration type not correct!",
			enumerationTypeName,
			(value as EnumPropertyValue).enumeration.name
		)
		assertEquals(
			"value not correct!",
			expected,
			(value as EnumPropertyValue).value
		)
	}
}
