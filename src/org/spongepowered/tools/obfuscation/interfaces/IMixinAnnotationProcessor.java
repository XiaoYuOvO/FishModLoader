/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.tools.obfuscation.interfaces;

import org.spongepowered.asm.util.ITokenProvider;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Interface for annotation processor core
 */
public interface IMixinAnnotationProcessor extends IMessagerSuppressible, IOptionProvider {

    /**
     * Detected compiler argument, specifies the behaviour of some operations
     * for compatibility purposes.
     */
    enum CompilerEnvironment {
        
        /**
         * Default environment
         */
        JAVAC,
        
        /**
         * Eclipse 
         */
        JDT
        
    }

    /**
     * Get the detected compiler environment
     */
    CompilerEnvironment getCompilerEnvironment();

    /**
     * Get the underlying processing environment
     */
    ProcessingEnvironment getProcessingEnvironment();

    /**
     * Get the obfuscation manager
     */
    IObfuscationManager getObfuscationManager();
    
    /**
     * Get the token provider
     */
    ITokenProvider getTokenProvider();
    
    /**
     * Get the type handle provider
     */
    ITypeHandleProvider getTypeProvider();

    /**
     * Get the javadoc provider
     */
    IJavadocProvider getJavadocProvider();

}
