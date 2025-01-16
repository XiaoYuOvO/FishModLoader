/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.xiaoyu233.fml.classloading;

import com.chocohead.mm.AsmTransformer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Manifest;

public interface KnotClassLoaderInterface {
	@SuppressWarnings("resource")
	static KnotClassLoaderInterface create() {
		return new KnotClassLoader().getDelegate();
	}

	void initializeTransformers(AsmTransformer asmTransformer);
	Set<Path> getCodeSource();
	ClassLoader getClassLoader();

	void addCodeSource(Path path);
	void addUrl(URL url);
	void setAllowedPrefixes(Path codeSource, String... prefixes);
	void setValidParentClassPath(Collection<Path> codeSources);

	Manifest getManifest(Path codeSource);

	void blockClassPrefix(String prefix);

    void whitelistClassPrefix(String prefix);

    void unlockBlocking();
	boolean isClassLoaded(String name);
	Class<?> loadIntoTarget(String name) throws ClassNotFoundException;
	byte[] getRawClassBytes(String name) throws IOException;
	byte[] getPreMixinClassBytes(String name);
	Optional<Path> findClassCodeSource(String className);
}
