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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.AccessUtils;
import me.itzsomebody.radon.utils.BytecodeUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Adds a synthetic modifier and bridge modifier if possible to attempt to hide code against some lower-quality
 * decompilers.
 *
 * @author ItzSomebody
 */
public class HideCode extends Transformer {
    private boolean hideClassesEnabled;
    private boolean hideMethodsEnabled;
    private boolean hideFieldsEnabled;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            if (isHideClassesEnabled()) {
                ClassNode classNode = classWrapper.classNode;

                if (!AccessUtils.isSynthetic(classNode.access) && !BytecodeUtils.hasAnnotations(classNode)) {
                    classNode.access |= ACC_SYNTHETIC;
                    counter.incrementAndGet();
                }
            }
            if (isHideMethodsEnabled()) {
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && !BytecodeUtils.hasAnnotations(methodWrapper.methodNode)).forEach(methodWrapper -> {
                    boolean atLeastOnce = false;
                    MethodNode methodNode = methodWrapper.methodNode;

                    if (!AccessUtils.isSynthetic(methodNode.access)) {
                        methodNode.access |= ACC_SYNTHETIC;
                        atLeastOnce = true;
                    }
                    if (!AccessUtils.isBridge(methodNode.access)) {
                        methodNode.access |= ACC_BRIDGE;
                        atLeastOnce = true;
                    }

                    if (atLeastOnce)
                        counter.incrementAndGet();

                });
            }
            if (isHideFieldsEnabled()) {
                classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                        && !BytecodeUtils.hasAnnotations(fieldWrapper.fieldNode)).forEach(fieldWrapper -> {
                    FieldNode fieldNode = fieldWrapper.fieldNode;

                    if (!AccessUtils.isSynthetic(fieldNode.access)) {
                        fieldNode.access |= ACC_SYNTHETIC;
                        counter.incrementAndGet();
                    }
                });
            }
        });

        Logger.stdOut(String.format("Hid %d members.", counter.get()));
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.HIDE_CODE;
    }

    @Override
    public String getName() {
        return "Hide code";
    }

    public boolean isHideClassesEnabled() {
        return hideClassesEnabled;
    }

    public void setHideClassesEnabled(boolean hideClassesEnabled) {
        this.hideClassesEnabled = hideClassesEnabled;
    }

    public boolean isHideMethodsEnabled() {
        return hideMethodsEnabled;
    }

    public void setHideMethodsEnabled(boolean hideMethodsEnabled) {
        this.hideMethodsEnabled = hideMethodsEnabled;
    }

    public boolean isHideFieldsEnabled() {
        return hideFieldsEnabled;
    }

    public void setHideFieldsEnabled(boolean hideFieldsEnabled) {
        this.hideFieldsEnabled = hideFieldsEnabled;
    }
}