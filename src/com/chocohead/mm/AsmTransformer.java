/*
 * Copyright 2019 Chocohead
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.chocohead.mm;

import com.chocohead.mm.EnumSubclasser.StructClass;
import com.chocohead.mm.api.ClassTinkerers;
import com.chocohead.mm.api.EnumAdder;
import com.chocohead.mm.api.EnumAdder.EnumAddition;
import com.google.common.collect.Maps;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
import org.spongepowered.asm.mixin.transformer.ext.extensions.ExtensionClassExporter;

import java.io.IOException;
import java.io.Serial;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class AsmTransformer {
	private static final int ACCESSES = ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_PRIVATE);
	final Map<String, String> enumStructParents = new HashMap<>();
	private final Map<String, Set<Consumer<ClassNode>>> classModifiers = new HashMap<>();
	private final Map<String, Consumer<ClassNode>> classReplacers = new HashMap<>() {
		@Serial
		private static final long serialVersionUID = -1226882557534215762L;
		private boolean skipGen = false;

		@Override
		public Consumer<ClassNode> put(String key, Consumer<ClassNode> value) {
			if (!skipGen && !classModifiers.containsKey(key)) classModifiers.put(key, new HashSet<>());
			return super.put(key, value);
		}

		@Override
		public void putAll(Map<? extends String, ? extends Consumer<ClassNode>> m) {
			skipGen = true;
			//Avoid squishing anything if it's already there, otherwise make an empty set
			classModifiers.putAll(Maps.asMap(m.keySet(), name -> classModifiers.getOrDefault(name, new HashSet<>())));
			super.putAll(m);
			skipGen = false;
		}
	};

	private final Set<EnumAdder> enumExtenders = new HashSet<>() {
		@Serial
		private static final long serialVersionUID = -2218861530200989346L;
		private boolean skipCheck = false;

		private void addTransformations(EnumAdder builder) {
			ClassTinkerers.addTransformation(builder.type, EnumExtender.makeEnumExtender(builder));

			for (EnumAddition addition : builder.getAdditions()) {
				if (addition.isEnumSubclass()) {
					ClassTinkerers.addReplacement(addition.structClass, EnumSubclasser.makeStructFixer(addition, builder.type));

					for (StructClass node : EnumSubclasser.getParentStructs(addition.structClass)) {
						String lastEnum = enumStructParents.put(node.name, builder.type);
						assert lastEnum == null || lastEnum.equals(builder.type);
					}
				}
			}

			enumStructParents.keySet().removeAll(classReplacers.keySet());
		}

		@Override
		public boolean add(EnumAdder builder) {
			if (!skipCheck) addTransformations(builder);
			return super.add(builder);
		}

		@Override
		public boolean addAll(Collection<? extends EnumAdder> builders) {
			skipCheck = true;
			for (EnumAdder builder : builders) addTransformations(builder);
			boolean out = super.addAll(builders);
			skipCheck = false;
			return out;
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(Predicate<? super EnumAdder> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
	};

	private static Consumer<ClassNode> makeAT(Set<String> transforms) {
		return node -> {
			//System.out.println("ATing " + node.name + " with " + transforms);
			if (transforms.remove("<*>")) {
				node.access = flipBits(node.access);

				for (InnerClassNode innerClass : node.innerClasses) {
					if (node.name.equals(innerClass.name)) {
						innerClass.access = flipBits(innerClass.access);
						break;
					}
				}
			}

			if (!transforms.isEmpty()) {
				for (MethodNode method : node.methods) {
					if (transforms.contains(method.name + method.desc)) {
						method.access = flipBits(method.access);
					}
					for (AbstractInsnNode insnNode : method.instructions) {
						if (insnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
							MethodInsnNode methodInsnNode = (MethodInsnNode) insnNode;

							if (!methodInsnNode.name.equals("<init>") && methodInsnNode.owner.equals(node.name) && transforms.contains(methodInsnNode.name + methodInsnNode.desc)) {
								// Private methods are normally invoked with INVOKESPECIAL
								// We want to make sure that any private -> public methods are invoked with INVOKEVIRTUAL, so that the JVM correctly handles potential inheritance
								methodInsnNode.setOpcode(Opcodes.INVOKEVIRTUAL);
							}
						}
					}
				}
			}
		};
	}
	private static int flipBits(int access) {
		access &= ACCESSES;
		access |= Opcodes.ACC_PUBLIC;
		access &= ~Opcodes.ACC_FINAL;
		return access;
	}

	private static void initializeSilkyAT() {
		Map<String, Set<String>> transforms = new HashMap<>();
		try {
			Enumeration<URL> urls = AsmTransformer.class.getClassLoader().getResources("silky.at");
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				//System.out.println("Found AT: " + url);

				try (Scanner scanner = new Scanner(url.openStream())) {
					//System.out.println("Made scanner");
					while (scanner.hasNextLine()) {
						String line = scanner.nextLine().trim();
						//System.out.println("On line: \"" + line + '\"');
						if (line.isEmpty() || line.startsWith("#")) continue;

						int split = line.indexOf(' ');
						String className, method;
						if (split > 0) {
							className = line.substring(0, split++);
							method = line.substring(split);
						} else {
							className = line;
							method = "<*>";
						}

						transforms.computeIfAbsent(className, k -> new HashSet<>()).add(method);
					}
					//System.out.println("Finished with scanner");
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error loading access transformers", e);
		}

		for (Entry<String, Set<String>> entry : transforms.entrySet()) {
			ClassTinkerers.addTransformation(entry.getKey(), makeAT(entry.getValue()));
		}
	}

	public void buildAndInitializeTransformer(Consumer<URL> urlAdder) {
		Map<String, byte[]> classGenerators = new HashMap<>();
		if (!enumStructParents.isEmpty()) {
			for (Entry<String, String> entry : enumStructParents.entrySet()) {
				ClassTinkerers.addReplacement(entry.getKey(), EnumSubclasser.makeStructFixer(entry.getKey(), entry.getValue()));
			}
		}

		ClassTinkerers.addURL(CasualStreamHandler.create(classGenerators));
		ClassTinkerers.INSTANCE.buildTinkerers(urlAdder, new UnremovableMap<>(classGenerators), new UnremovableMap<>(classReplacers), new UnremovableMap<>(classModifiers), enumExtenders);

		//System.out.println("Loaded initially with: " + classModifiers);

		Object transformer = MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
		if (transformer == null) throw new IllegalStateException("Not running with a transformer?");

		Extensions extensions = null;
		try {
			for (Field f : transformer.getClass().getDeclaredFields()) {
				if (f.getType() == Extensions.class) {
					f.setAccessible(true); //Knock knock, we need this
					extensions = (Extensions) f.get(transformer);
					break;
				}
			}

			if (extensions == null) {
				String foundFields = Arrays.stream(transformer.getClass().getDeclaredFields()).map(f -> f.getType() + " " + f.getName()).collect(Collectors.joining(", "));
				throw new NoSuchFieldError("Unable to find extensions field, only found " + foundFields);
			}
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Running with a transformer that doesn't have extensions?", e);
		}

		ExtensionClassExporter exporter = extensions.getExtension(ExtensionClassExporter.class);
		CasualStreamHandler.dumper = (name, bytes) -> {
			ClassNode node = new ClassNode(); //Read the bytes in as per TreeTransformer#readClass(byte[])
			new ClassReader(bytes).accept(node, ClassReader.EXPAND_FRAMES);
			exporter.export(MixinEnvironment.getCurrentEnvironment(), name, false, node);
		};
		System.out.println("ClassTinkerers initialized.");
	}

	public Optional<Consumer<ClassNode>> getClassModifier(String className){
		final String newClassName = className.replace('.', '/');
		if (!classModifiers.containsKey(newClassName) && !classReplacers.containsKey(newClassName)) return Optional.empty();
		Consumer<ClassNode> classModifiers = node -> applyClassModifies(newClassName, node);
		return Optional.ofNullable(classReplacers.get(newClassName)).map(replacer -> replacer.andThen(classModifiers)).or(() -> Optional.of(classModifiers));
	}

	private void applyClassModifies(String targetClassName, ClassNode targetClass) {
		Set<Consumer<ClassNode>> transformations = classModifiers.get(targetClassName);
		if (transformations != null) {
			System.out.println("[ClassTinkerer] Modifying class:" + targetClassName);
			for (Consumer<ClassNode> transformer : transformations) {
				transformer.accept(targetClass);
			}
		}
	}
}