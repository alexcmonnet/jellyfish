/*
 * generated by Xtext 2.10.0
 */
package com.ngc.seaside.systemdescriptor.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;

import com.google.inject.Inject;

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
public class SystemDescriptorScopeProvider extends AbstractSystemDescriptorScopeProvider {

	@Inject
	private PackageBasedImportedNamespaceAwareLocalScopeProvider provider;
	
	@Override
	public IScope getScope(EObject context, EReference reference) {
		// Use our customized imported namespace aware scope provider.
		return provider.getScope(context, reference);
	}
}
