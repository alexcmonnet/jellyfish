/**
 * UNCLASSIFIED
 * Northrop Grumman Proprietary
 * ____________________________
 *
 * Copyright (C) 2019, Northrop Grumman Systems Corporation
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property of
 * Northrop Grumman Systems Corporation. The intellectual and technical concepts
 * contained herein are proprietary to Northrop Grumman Systems Corporation and
 * may be covered by U.S. and Foreign Patents or patents in process, and are
 * protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from Northrop Grumman.
 */
package com.ngc.seaside.systemdescriptor.tests.properties

import com.google.inject.Inject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model
import com.ngc.seaside.systemdescriptor.systemDescriptor.Package
import com.ngc.seaside.systemdescriptor.tests.SystemDescriptorInjectorProvider
import com.ngc.seaside.systemdescriptor.tests.resources.Datas
import com.ngc.seaside.systemdescriptor.tests.resources.Models
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.util.ResourceHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import static org.junit.Assert.*
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage

@RunWith(XtextRunner)
@InjectWith(SystemDescriptorInjectorProvider)
class ModelFieldPropertiesParsingTest {

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
			Models.CLOCK,
			Models.LINKED_CLOCK,
			Models.WIRED_SPEAKER,
			Datas.ZONED_TIME
		)
		validationTester.assertNoIssues(requiredResources)
	}

	@Test
	def void testDoesParseModelWithPartProperties() {
		val source = '''
			package clocks.models

			import clocks.models.part.Clock
			import clocks.models.part.LinkedClock

			model BigClock refines LinkedClock {
				parts {
					Clock part {
						properties {
							int intField
						}
					}
					refine clock {
						properties {
							string stringField
						}
					}
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		val model = result.element as Model
		var properties = model.parts.declarations.get(0).definition.properties
		assertNotNull("did not parse properties", properties)

		val declaration = properties.declarations.get(0)
		assertEquals("property name not correct", "intField", declaration.name)

		val refinedProperties = model.parts.declarations.get(1).definition.properties
		assertNotNull("did not parse properties", refinedProperties)

		val refinedDeclaration = refinedProperties.declarations.get(0)
		assertEquals("property name not correct", "stringField", refinedDeclaration.name)
	}

	@Test
	def void testDoesParseModelWithRequiresProperties() {
		val source = '''
			package clocks.models

			import clocks.models.part.Clock

			model BigClock {
				requires {
					Clock clock {
						properties {
							int intField 
							intField = 20
						}
					}
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		val model = result.element as Model
		var properties = model.requires.declarations.get(0).definition.properties
		assertNotNull("did not parse properties", properties)

		val declaration = properties.declarations.get(0)
		assertEquals("property name not correct", "intField", declaration.name)
	}

    @Test
    def void testDoesParseModelWithRequiresPropertiesBeingDeclaredAndAssigned() {
        val source = '''
            package clocks.models
            
            import clocks.models.part.LinkedClock
            
            model BigClock refines LinkedClock{
                requires {
                   refine reqClock {
                        properties {
                            int intRequiresField
                            intRequiresField = 100
                        }
                    }
                }
            }
        '''

        val result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
    }
    
    @Test
    def void testDoesParseModelWithPartsPropertiesBeingDeclaredAndAssigned() {
        val source = '''
            package clocks.models
            
            import clocks.models.part.LinkedClock
            
            model BigClock refines LinkedClock{
                parts {
                   refine clockD {
                        properties {
                        int intMyPartsField
                        intMyPartsField = 100
                        }
                    }
                }
            }
        '''

        val result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
    }

	@Test
	def void testDoesParseModelWithRefinedRequiresProperties() {
		val source = '''
			package clocks.models

			import clocks.models.part.LinkedClock

			model BigClock refines LinkedClock {
				requires {
					refine requiresEmptyModel {
						properties {
							int intField
						}
					}
				}
			}
		'''

		val result = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(result)
		validationTester.assertNoIssues(result)

		val model = result.element as Model
		var properties = model.requires.declarations.get(0).definition.properties
		assertNotNull("did not parse properties", properties)

		val declaration = properties.declarations.get(0)
		assertEquals("property name not correct", "intField", declaration.name)
	}
	
	@Test
    def void testDoesParseModelWithRefinedRequiresPropertiesSet() {
        val source = '''
            package clocks.models
            
            import clocks.models.part.LinkedClock
            
            model BigClock refines LinkedClock {
                requires {
                    refine reqClock {
                        properties {
                            intRequiredField = 1
                        }
                    }
                }
            }
        '''

        val result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
        
    }
    
    @Test
    def void testDoesParseModelWithRefinedPartsPropertiesSet() {
        val source = '''
            package clocks.models
            
            import clocks.models.part.LinkedClock
            
            model BigClock refines LinkedClock {
                parts {
                    refine clockD {
                        properties {
                            intPartsField = 100
                        }
                    }
                }
            }
        '''

        val result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
        
    }
    
    @Test
    def void testDoesParseModelWithPartPropertiesDirectlyFromModel() {
    	val source = '''
            package clocks.models
            
            import clocks.models.part.WiredSpeaker
            
            model MyModel {
                parts {
                    WiredSpeaker speaker {
                    	properties {
                    		wireGauge = 19
                    	}
                    }
                }
            }
        '''

        val result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
    }
    
    @Test
    def void testDoesParseModelWithRequirementPropertiesDirectlyFromModel() {
    	val source = '''
            package clocks.models
            
            import clocks.models.part.WiredSpeaker
            
            model MyModel {
                requires {
                    WiredSpeaker speaker {
                    	properties {
                    		wireGauge = 19
                    	}
                    }
                }
            }
        '''

        val result = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(result)
        validationTester.assertNoIssues(result)
    }

	@Test
	def void testDoesNotParseModelWithInputProperties() {
		val source = '''
			package clocks.models

			import clocks.datatypes.ZonedTime

			model BigClock {
				input {
					ZonedTime input1 {
						properties {}
					}
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PROPERTIES,
			null
		)
	}

	@Test
	def void testDoesNotParseModelWithOutputProperties() {
		val source = '''
			package clocks.models

			import clocks.datatypes.ZonedTime

			model BigClock {
				output {
					ZonedTime output1 {
						properties {}
					}
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PROPERTIES,
			null
		)
	}

	@Test
	def void testDoesNotParseDataWithProperties() {
		val source = '''
			package clocks.models

			data Data {
				int field {
					properties {}
				}
			}
		'''

		val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
		assertNotNull(invalidResult)
		validationTester.assertError(
			invalidResult,
			SystemDescriptorPackage.Literals.PROPERTIES,
			null
		)
	}
	
	@Test
    def void testDoesNotParsePartsWithRedifnedPropety() {
        val source = '''
            package clocks.models
                        
            import clocks.models.part.LinkedClock
            
            model BigClock refines LinkedClock {
                parts {
                    refine clockD {
                        properties {
                            int intPartsField
                        }
                    }
                }
            }
        '''

        val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)
        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.PROPERTIES,
            null
        )
    }
    
    @Test
    def void testDoesNotParseRequiresWithRedifnedPropety() {
        val source = '''
            package clocks.models
                        
            import clocks.models.part.LinkedClock
            
            model BigClock refines LinkedClock {
                requires {
                    refine reqClock {
                        properties {
                           int intRequiredField
                        }
                    }
                }
            }
        '''

        val invalidResult = parseHelper.parse(source, requiredResources.resourceSet)
        assertNotNull(invalidResult)
        validationTester.assertError(
            invalidResult,
            SystemDescriptorPackage.Literals.PROPERTIES,
            null
        )
    }

}
