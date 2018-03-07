package com.ngc.seaside.systemdescriptor.scoping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;

import com.ngc.seaside.systemdescriptor.systemDescriptor.BasePartDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.BaseRequireDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.FieldDeclaration;
import com.ngc.seaside.systemdescriptor.systemDescriptor.FieldReference;
import com.ngc.seaside.systemdescriptor.systemDescriptor.LinkableExpression;
import com.ngc.seaside.systemdescriptor.systemDescriptor.Model;
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage;

/**
 * The scope provider for the System Descriptor language.
 */
public class SystemDescriptorScopeProvider extends AbstractDeclarativeScopeProvider {

	/**
	 * Provides scope for a link expression of the form
	 * {@code link someInput to somePart.someMoreInput}.
	 *
	 * @param context
	 * @param reference
	 * @return scope for a link expression
	 */
	public IScope scope_LinkableExpression_tail(LinkableExpression context, EReference reference) {
		// Get the field reference thus far. If we are parsing the expression
		// link someInput to somePart.someMoreInput
		// than ref will equal the part "somePart".
		FieldReference ref = (FieldReference) context.getRef();
		// Get the field declaration the reference is pointing to.
		FieldDeclaration fieldDeclaration = ref.getFieldDeclaration();

		if (fieldDeclaration.getName() == null) {
			List<INode> nodes = NodeModelUtils.findNodesForFeature(ref,
					SystemDescriptorPackage.Literals.FIELD_REFERENCE__FIELD_DECLARATION);
			String fieldName = NodeModelUtils.getTokenText(nodes.get(0));
			System.out.println(fieldName);
			fieldDeclaration = getFieldWithName(fieldName, context.eContainer().eContainer().eContainer());
		}

		IScope scope;

		// We need to determine the type of the field declaration. Right now we
		// only support nested expressions for models. More requires and parts
		// can declare other models as fields. Input and output must declare
		// data as fields. Since we can't yet reference the contents of data,
		// we don't have to worry about referencing input or output.
		if (fieldDeclaration.eClass().equals(SystemDescriptorPackage.Literals.BASE_REQUIRE_DECLARATION)) {
			BaseRequireDeclaration casted = (BaseRequireDeclaration) fieldDeclaration;
			// Include all field declarations of the referenced model in the
			// scope.
			scope = Scopes.scopeFor(getLinkableFieldsFrom(casted.getType()));
		} else if (fieldDeclaration.eClass().equals(SystemDescriptorPackage.Literals.BASE_PART_DECLARATION)) {
			// Include all field declarations of the referenced model in the
			// scope.
			BasePartDeclaration casted = (BasePartDeclaration) fieldDeclaration;
			scope = Scopes.scopeFor(getLinkableFieldsFrom(casted.getType()));
		} else if (fieldDeclaration.eClass().equals(SystemDescriptorPackage.Literals.REFINED_LINK_DECLARATION)) {
			// Include all field declarations of the referenced model in the
			// scope.
			BasePartDeclaration casted = (BasePartDeclaration) fieldDeclaration;
			scope = Scopes.scopeFor(getLinkableFieldsFrom(casted.getType()));
		} else {
			// Otherwise, do the default behavior.
			scope = delegateGetScope(context, reference);
		}

		return scope;
	}

	/**
	 * Gets all field declarations that can be referenced that are contained by
	 * the given model.
	 *
	 * @param model
	 * @return all field declarations that can be referenced that are contained
	 *         by the given model
	 */
	private static Collection<FieldDeclaration> getLinkableFieldsFrom(Model model) {
		// TODO TH: we can limit the items in scope by examining the type of the
		// item on the left hand side of the expression.
		Collection<FieldDeclaration> fields = new ArrayList<>();

		do {
			if (model.getInput() != null) {
				fields.addAll(model.getInput().getDeclarations());
			}
			if (model.getOutput() != null) {
				fields.addAll(model.getOutput().getDeclarations());
			}
			if (model.getRequires() != null) {
				fields.addAll(model.getRequires().getDeclarations());
			}
			if (model.getParts() != null) {
				fields.addAll(model.getParts().getDeclarations());
			}
			model = model.getRefinedModel();
		} while (model != null);

		return fields;
	}

	private static FieldDeclaration getFieldWithName(String fieldName, EObject model) {
		FieldDeclaration fieldDeclaration = null;

		Model parentModel = ((Model) model).getRefinedModel();

		while (parentModel != null) {
			// Part Declaration
			// TODO TH: this should reference parts, requirements, inputs
			// fields, and output fields.
			if (parentModel.getParts() != null &&
					parentModel.getParts().getDeclarations() != null) {
				for (FieldDeclaration fieldDec : parentModel.getParts().getDeclarations()) {
					if (fieldDec.getName().equals(fieldName)) {
						fieldDeclaration = fieldDec;
						break;
					}
				}
			}
			parentModel = parentModel.getRefinedModel();
		}

		return fieldDeclaration;
	}
}
