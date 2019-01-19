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
package com.ngc.seaside.systemdescriptor.formatting2

import com.ngc.seaside.systemdescriptor.systemDescriptor.PartDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.Parts
import org.eclipse.xtext.formatting2.IFormattableDocument
import com.ngc.seaside.systemdescriptor.systemDescriptor.SystemDescriptorPackage
import com.ngc.seaside.systemdescriptor.systemDescriptor.BasePartDeclaration
import com.ngc.seaside.systemdescriptor.systemDescriptor.RefinedPartDeclaration

class PartsFormatter extends AbstractSystemDescriptorFormatter {
    def dispatch void format(Parts parts, extension IFormattableDocument document) {
        var begin = parts.regionFor.keyword('parts')
        var end = parts.regionFor.keyword('}').append[newLines = 2]
        interior(begin, end)[indent]

        parts.regionFor.keyword('{').prepend[oneSpace].append[newLine]

        for (PartDeclaration d : parts.declarations) {
            d.format
            if (d.definition !== null && d != parts.declarations.last) {
                d.append[newLines = 2]
            } else {
                d.append[newLine]
            }
        }
    }

    def dispatch void format(PartDeclaration declaration, extension IFormattableDocument document) {
        formatDeclaration(declaration, document)
    }
    
    def dispatch void format(BasePartDeclaration declaration, extension IFormattableDocument document) {
    	formatDeclaration(declaration, document)
    	declaration.regionFor.feature(SystemDescriptorPackage.Literals.BASE_PART_DECLARATION__TYPE)
    		.prepend[noSpace]
    		.append[oneSpace]
    }
    
    def dispatch void format(RefinedPartDeclaration declaration, extension IFormattableDocument document) {
    	formatDeclaration(declaration, document)
    	declaration.regionFor.keyword('refine')
    		.prepend[noSpace]
    		.append[oneSpace]
    }
    
    private def void formatDeclaration(PartDeclaration declaration, extension IFormattableDocument document) {
    	declaration.prepend[noSpace]

		if(declaration.definition !== null) {
			declaration.regionFor.feature(SystemDescriptorPackage.Literals.FIELD_DECLARATION__NAME)
				.append[oneSpace]
			declaration.definition.format
		} else {
			declaration.regionFor.feature(SystemDescriptorPackage.Literals.FIELD_DECLARATION__NAME)
				.prepend[newLines = 0]
				.append[newLine]
		}
    }
}