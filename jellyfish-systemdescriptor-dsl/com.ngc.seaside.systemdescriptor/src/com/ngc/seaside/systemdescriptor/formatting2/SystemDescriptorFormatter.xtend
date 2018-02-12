/*
 * generated by Xtext 2.10.0
 */
package com.ngc.seaside.systemdescriptor.formatting2

import com.ngc.seaside.systemdescriptor.systemDescriptor.EnumerationValueDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.Import
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model
import com.ngc.seaside.systemdescriptor.systemDescriptor.Member
import com.ngc.seaside.systemdescriptor.systemDescriptor.Metadata
import com.ngc.seaside.systemdescriptor.systemDescriptor.JsonObject
import com.ngc.seaside.systemdescriptor.systemDescriptor.Input
import com.ngc.seaside.systemdescriptor.systemDescriptor.InputDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.Output
import com.ngc.seaside.systemdescriptor.systemDescriptor.OutputDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.Scenario
import com.ngc.seaside.systemdescriptor.systemDescriptor.GivenDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.ThenDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.WhenDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.Parts
import com.ngc.seaside.systemdescriptor.systemDescriptor.PartDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.Links
import com.ngc.seaside.systemdescriptor.systemDescriptor.LinkDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.WhenStep
import com.ngc.seaside.systemdescriptor.systemDescriptor.ThenStep
import com.ngc.seaside.systemdescriptor.systemDescriptor.Data
import com.ngc.seaside.systemdescriptor.systemDescriptor.DataFieldDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.PrimitiveDataFieldDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.ReferencedDataModelFieldDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.JsonValue
import com.ngc.seaside.systemdescriptor.systemDescriptor.GivenStep
import com.ngc.seaside.systemdescriptor.systemDescriptor.Enumeration
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import com.ngc.seaside.systemdescriptor.systemDescriptor.DeclarationDefinition

class SystemDescriptorFormatter extends AbstractFormatter2 {

	var DEBUG_MODE = false;

	def dispatch void format(com.ngc.seaside.systemdescriptor.systemDescriptor.Package _package,
		extension IFormattableDocument document) {

		_package.regionFor.keyword('package').prepend[noIndentation]
		_package.regionFor.feature(SystemDescriptorPackage.Literals.PACKAGE__NAME).append[setNewLines(2)]

		if (_package.getImports().size != 0) {
			
			for (Import imports : _package.getImports()) {
				if (imports == _package.getImports().last) {
					imports.append[setNewLines(2)];
				} else {
					imports.append[setNewLines(1)];
				}
			}
		}
		_package.getElement.format;
	}
	
	def dispatch void format(Enumeration enumeration, extension IFormattableDocument document) {
		debugLog("Entering method: format(ENUM)")
		enumeration.regionFor.keyword('enum').prepend[noIndentation]
		enumeration.regionFor.keyword('{').prepend[oneSpace].append[newLine]
		
		if (enumeration.metadata !== null) {
			enumeration.metadata.format
		}
		
		for (EnumerationValueDeclaration value : enumeration.values) {
			value.format
			value.regionFor.keyword(',').prepend[noSpace]
			value.append[newLine]
		}
		
		var begin = enumeration.regionFor.keyword('enum')
		var end = enumeration.regionFor.keyword('}')
		interior(begin, end)[indent]
	}

	def dispatch void format(EnumerationValueDeclaration value,
		extension IFormattableDocument document) {
		if (value.definition.metadata !== null) {
			value.definition.metadata.format;
			value.definition.metadata.prepend[oneSpace]
		}
	}

	def dispatch void format(Model model, extension IFormattableDocument document) {
		debugLog("Entering method: format(MODEL)");

		model.regionFor.keyword('model').prepend[noIndentation]
		model.regionFor.keyword('{').prepend[oneSpace].append[newLine]

		if (model.getMetadata() !== null) {
			model.getMetadata().format;
		}

		var modelInput = model.getInput();
		modelInput.format;

		var modelOutput = model.getOutput();
		modelOutput.format;

		var modelParts = model.getParts();
		modelParts.format;

		var modelLinks = model.getLinks();
		modelLinks.format;

		var modelScenarios = model.getScenarios();
		for (Scenario scenario : modelScenarios) {
			scenario.format;
		}

		var begin = model.regionFor.keyword('model')
		var end = model.regionFor.keyword('}')
		interior(begin, end)[indent]

	}

	def dispatch void format(Metadata mData, extension IFormattableDocument document) {
		debugLog("Entering method: format(METADATA)");
		mData.getJson().format
		mData.append[setNewLines(2)]
	}

	def dispatch void format(JsonObject json, extension IFormattableDocument document) {
		debugLog("Entering method: format(JSON)");

		var begin = json.regionFor.keyword('{').prepend[oneSpace]
		var end = json.regionFor.keyword('}')
		interior(begin, end)[indent]

		for (Member member : json.getMembers()) {
			member.format
			if (member == json.getMembers().last) {
				member.append[newLine]
			}
		}
	}

	def dispatch void format(Member member, extension IFormattableDocument document) {
		debugLog("Entering method: format(MEMBER), key is " + member.getKey());
		
//		var begin = member.regionFor.keyword('{')
//		var end = member.regionFor.keyword('}')
//		interior(begin, end)[indent]
		
		member.regionFor.keyword(':').prepend[noSpace]
		member.getValue.format.prepend[oneSpace]
		
		member.prepend[newLine]
	}

	def dispatch void format(Input input, extension IFormattableDocument document) {
		debugLog("Entering method: format(INPUT)");

		var begin = input.regionFor.keyword('input')
		var end = input.regionFor.keyword('}')

		input.regionFor.keyword('{').prepend[oneSpace].append[newLine]

		for (InputDeclaration dec : input.getDeclarations()) {
			dec.format;
			if (dec != input.getDeclarations.last && dec.definition.metadata !== null) {
				dec.append[setNewLines(2)]
			} else {
				dec.append[newLine]
			}
		}

		interior(begin, end)[indent]

		input.regionFor.keyword('}').append[setNewLines(2)];
	}

	def dispatch void format(InputDeclaration inputDec, extension IFormattableDocument document) {
		debugLog("Entering method: format(INPUT_DECLARATION)");

		if (inputDec.definition !== null) {
			inputDec.definition.format
		}
	}
	
	def dispatch void format(DeclarationDefinition definition, extension IFormattableDocument document) {
		if (definition.metadata !== null) {
			definition.metadata.format
		}
	}

	def dispatch void format(Output output, extension IFormattableDocument document) {
		debugLog("Entering method: format(OUTPUT)");

		var begin = output.regionFor.keyword('output')
		var end = output.regionFor.keyword('}')

		output.regionFor.keyword('{').prepend[oneSpace].append[newLine]

		for (OutputDeclaration dec : output.getDeclarations()) {
			dec.format;
			if (dec != output.getDeclarations.last && dec.definition.metadata !== null) {
				dec.append[setNewLines(2)]
			} else {
				dec.append[newLine]
			}
		}

		interior(begin, end)[indent]

		output.regionFor.keyword('}').append[setNewLines(2)];

	}

	def dispatch void format(OutputDeclaration outputDec, extension IFormattableDocument document) {
		debugLog("Entering method: format(OUTPUT_DECLARATION)");

		if (outputDec.definition.metadata !== null) {
			outputDec.definition.metadata.format
		}
	}

	def dispatch void format(Scenario scenario, extension IFormattableDocument document) {
		debugLog("Entering method: format(SCENARIO)");

		scenario.regionFor.keyword('{').prepend[oneSpace].append[newLine]

		if (scenario.getMetadata() !== null) {
			scenario.getMetadata().format;
		}

		if (scenario.getGiven() !== null) {
			scenario.getGiven().format;
		}

		if (scenario.getWhen() !== null) {
			scenario.getWhen().format;
		}

		if (scenario.getThen() !== null) {
			scenario.getThen().format;
		}

		var begin = scenario.regionFor.keyword('scenario')
		var end = scenario.regionFor.keyword('}')
		interior(begin, end)[indent]

		scenario.append[setNewLines(2)]
	}
	
	def dispatch void format(GivenDeclaration given, extension IFormattableDocument document) {
		debugLog("Entering method: format(GIVEN_DECLARATION)")

		for (GivenStep step : given.getSteps()) {
			given.append[newLine]
			step.format
		}

		given.append[newLine];
	}

	def dispatch void format(GivenStep step, extension IFormattableDocument document) {
		debugLog("Entering method: format(GIVEN_STEP)")
		step.append[newLine]
	}

	def dispatch void format(WhenDeclaration when, extension IFormattableDocument document) {
		debugLog("Entering method: format(WHEN_DECLARATION)")

		for (WhenStep step : when.getSteps()) {
			when.append[newLine]
			step.format
		}

		when.append[newLine];
	}

	def dispatch void format(WhenStep step, extension IFormattableDocument document) {
		debugLog("Entering method: format(WHEN_STEP)")
		step.append[newLine]
	}

	def dispatch void format(ThenDeclaration then, extension IFormattableDocument document) {
		debugLog("Entering method: format(THEN_DECLARATION)")

		for (ThenStep step : then.getSteps()) {
			step.append[newLine]
		}

		then.append[newLine]
	}

	def dispatch void format(Parts parts, extension IFormattableDocument document) {
		debugLog("Entering method: format(PARTS)")

		var begin = parts.regionFor.keyword('parts')
		var end = parts.regionFor.keyword('}')

		parts.regionFor.keyword('{').prepend[oneSpace].append[newLine]

		for (PartDeclaration dec : parts.getDeclarations()) {
			dec.format;
			dec.append[newLine]
		}

		interior(begin, end)[indent]

		parts.regionFor.keyword('}').append[setNewLines(2)];
	}

	def dispatch void format(PartDeclaration dec, extension IFormattableDocument document) {
		debugLog("Entering method: format(PARTS_DECLARATION)")
	}

	def dispatch void format(Links links, extension IFormattableDocument document) {
		debugLog("Entering method: format(LINKS)")

		var begin = links.regionFor.keyword('links')
		var end = links.regionFor.keyword('}')

		links.regionFor.keyword('{').prepend[oneSpace].append[newLine]

		for (LinkDeclaration dec : links.getDeclarations()) {
			dec.format;
		}

		interior(begin, end)[indent]

		links.regionFor.keyword('}').append[setNewLines(2)];
	}

	def dispatch void format(LinkDeclaration dec, extension IFormattableDocument document) {
		debugLog("Entering method: format(LINK_DECLARATION)")
		dec.regionFor.keyword('link').append[oneSpace]
		if (dec.getName() != null && !dec.getName().isEmpty()) {
			dec.getSource().prepend[dec.getName]
			dec.getSource().prepend[oneSpace]
		}
		dec.regionFor.keyword('->').prepend[oneSpace].append[oneSpace]
		dec.getSource().append[oneSpace]
		dec.getTarget().append[newLine]
	}

	def dispatch void format(Data data, extension IFormattableDocument document) {
		debugLog("Entering method: format(DATA)")
		var begin = data.regionFor.keyword('data')
		var end = data.regionFor.keyword('}')
		
		data.prepend[setNewLines(2)]

		if (data.getMetadata() !== null) {
			data.getMetadata().format;
		}

		for (DataFieldDeclaration dec : data.getFields()) {
			dec.prepend[newLine]
			dec.format;
		}

		interior(begin, end)[indent]
	}

	def dispatch void format(PrimitiveDataFieldDeclaration pDec, extension IFormattableDocument document) {
		debugLog("Entering method: format(PRIMITITVE_DATA_FIELD_DECLARATION)")
		if (pDec.definition.metadata !== null) {
			pDec.definition.metadata.format
		}
		
		pDec.prepend[noSpace].append[newLine]

	}

	def dispatch void format(ReferencedDataModelFieldDeclaration rDec, extension IFormattableDocument document) {
		debugLog("Entering method: format(REFERENCED_DATA_MODEL_FIELD_DECLARATION)")
		rDec.prepend[noSpace].append[newLine]
	}
	
	def dispatch void format(JsonValue jsonVal, extension IFormattableDocument document) {
		debugLog("Entering method: format(JSON_VALUE)")
		jsonVal.getValue.format
	}
	
	private def void debugLog(String msg) {
		if (DEBUG_MODE) {
			System.out.println(msg)
		}
	}

// TODO: implement for Data, RequireDeclaration, Requires, Enumeration, ArrayValue, Array, LinkableExpression
}
