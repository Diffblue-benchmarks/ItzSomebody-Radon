/*
 * Copyright (C) 2018 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import org.objectweb.asm.tree.MethodNode;

/**
 * Destroys the local variable table.
 *
 * @author ItzSomebody
 */
public class LocalVariableRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && methodWrapper.methodNode.localVariables != null).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;

                    counter.addAndGet(methodNode.localVariables.size());
                    methodNode.localVariables = null;
                }));

        Logger.stdOut(String.format("Removed %d local variables.", counter.get()));
    }

    @Override
    public String getName() {
        return "Local variables";
    }
}