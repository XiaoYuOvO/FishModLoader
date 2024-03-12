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

package net.xiaoyu233.fml.mixin.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.logging.LoggerAdapterAbstract;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class MixinLogger extends LoggerAdapterAbstract {
	private static final Logger LOGGER = LogManager.getLogger("Mixin");
	private static final Map<String, ILogger> LOGGER_MAP = new ConcurrentHashMap<>();
	private static final Map<Level, org.apache.logging.log4j.Level> LEVEL_MAP = createLevelMap();


	MixinLogger(String name) {
		super(name);
	}

	static ILogger get(String name) {
		return LOGGER_MAP.computeIfAbsent(name, MixinLogger::new);
	}

	private static org.apache.logging.log4j.Level translateLevel(Level level) {
		return LEVEL_MAP.getOrDefault(level, org.apache.logging.log4j.Level.INFO);
	}

	private static Map<Level, org.apache.logging.log4j.Level> createLevelMap() {
		Map<Level, org.apache.logging.log4j.Level> ret = new EnumMap<>(Level.class);

		ret.put(Level.FATAL, org.apache.logging.log4j.Level.ERROR);
		ret.put(Level.ERROR, org.apache.logging.log4j.Level.ERROR);
		ret.put(Level.WARN, org.apache.logging.log4j.Level.WARN);
		ret.put(Level.INFO, org.apache.logging.log4j.Level.INFO);
		ret.put(Level.DEBUG, org.apache.logging.log4j.Level.DEBUG);
		ret.put(Level.TRACE, org.apache.logging.log4j.Level.TRACE);

		return ret;
	}

	@Override
	public String getType() {
		return "Fabric Mixin Logger";
	}

	@Override
	public void catching(Level level, Throwable t) {
		log(level, "Catching ".concat(t.toString()), t);
	}

	@Override
	public void log(Level level, String message, Object... params) {
		org.apache.logging.log4j.Level fabricLevel = translateLevel(level);

		Throwable exc = null;

		if (params != null && params.length > 0) {
			if (message == null) {
				if (params[0] instanceof Throwable) exc = (Throwable) params[0];
			} else {
				// emulate Log4J's {} tokens and \ escapes
				StringBuilder sb = new StringBuilder(message.length() + 20);
				int paramIdx = 0;
				boolean escaped = false;

				for (int i = 0, max = message.length(); i < max; i++) {
					char c = message.charAt(i);

					if (escaped) {
						sb.append(c);
						escaped = false;
					} else if (c == '\\' && i + 1 < max) {
						escaped = true;
					} else if (c == '{' && i + 1 < max && message.charAt(i + 1) == '}' && paramIdx < params.length) { // unescaped {} with matching param idx
						Object param = params[paramIdx++];

						if (param == null) {
							sb.append("null");
						} else if (param.getClass().isArray()) {
							String val = Arrays.deepToString(new Object[] { param });
							sb.append(val, 1, val.length() - 1);
						} else {
							sb.append(param);
						}

						i++; // skip over }
					} else {
						sb.append(c);
					}
				}

				message = sb.toString();

				if (paramIdx < params.length && params[params.length - 1] instanceof Throwable) {
					exc = (Throwable) params[params.length - 1];
				}
			}
		}

		LOGGER.log(fabricLevel, message, exc);
	}

	@Override
	public void log(Level level, String message, Throwable t) {
		LOGGER.log(translateLevel(level), message, t);
	}

	@Override
	public <T extends Throwable> T throwing(T t) {
		log(Level.ERROR, "Throwing ".concat(t.toString()), t);

		return t;
	}
}
