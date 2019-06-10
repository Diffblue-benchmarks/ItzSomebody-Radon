package me.itzsomebody.radon.utils;

import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.utils.ASMUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.powermock.api.mockito.PowerMockito;

public class ASMUtilsTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testIsReturn() {
    Assert.assertFalse(ASMUtils.isReturn(0));
    Assert.assertFalse(ASMUtils.isReturn(178));
    Assert.assertTrue(ASMUtils.isReturn(176));
  }

  @Test
  public void testIsInstruction() {
    Assert.assertFalse(ASMUtils.isInstruction(
        PowerMockito.mock(FrameNode.class)));
    Assert.assertFalse(ASMUtils.isInstruction(
        PowerMockito.mock(LabelNode.class)));
    Assert.assertFalse(ASMUtils.isInstruction(
        PowerMockito.mock(LineNumberNode.class)));
    Assert.assertTrue(ASMUtils.isInstruction(
        PowerMockito.mock(InsnNode.class)));
  }

  @Test
  public void testIsIntInsn() throws Exception {
    Assert.assertTrue(ASMUtils.isIntInsn(new LdcInsnNode(0)));
    Assert.assertFalse(ASMUtils.isIntInsn(new LdcInsnNode(null)));
    Assert.assertFalse(ASMUtils.isIntInsn(null));
  }

  @Test
  public void testIsLongInsn() throws Exception {
    Assert.assertTrue(ASMUtils.isLongInsn(new LdcInsnNode(0L)));
    Assert.assertFalse(ASMUtils.isLongInsn(new LdcInsnNode(null)));
  }

  @Test
  public void testIsFloatInsn() throws Exception {
    Assert.assertTrue(ASMUtils.isFloatInsn(new LdcInsnNode(0.0f)));
    Assert.assertFalse(ASMUtils.isFloatInsn(new LdcInsnNode(null)));
  }

  @Test
  public void testIsDoubleInsn() throws Exception {
    Assert.assertTrue(ASMUtils.isDoubleInsn(new LdcInsnNode(0.0)));
    Assert.assertFalse(ASMUtils.isDoubleInsn(new LdcInsnNode(null)));
  }

  @Test
  public void testGetNumberInsnInt() {
    AbstractInsnNode actual = ASMUtils.getNumberInsn(4);
    Assert.assertEquals(7, actual.getOpcode());
    
    actual = ASMUtils.getNumberInsn(6);
    Assert.assertEquals(16, actual.getOpcode());
    
    actual = ASMUtils.getNumberInsn(8198);
    Assert.assertEquals(17, actual.getOpcode());

    actual = ASMUtils.getNumberInsn(134_217_734);
    Assert.assertEquals(18, actual.getOpcode());
  }

  @Test
  public void testGetNumberInsnLong() {
    AbstractInsnNode actual = ASMUtils.getNumberInsn(0L);
    Assert.assertEquals(9, actual.getOpcode());
    
    actual = ASMUtils.getNumberInsn(2L);
    Assert.assertEquals(18, actual.getOpcode());
  }

  @Test
  public void testGetNumberInsnFloat() {
    AbstractInsnNode actual = ASMUtils.getNumberInsn(0.0f);
    Assert.assertEquals(11, actual.getOpcode());
    
    actual = ASMUtils.getNumberInsn(11.0f);
    Assert.assertEquals(18, actual.getOpcode());
  }

  @Test
  public void testGetNumberInsnDouble() {
    AbstractInsnNode actual = ASMUtils.getNumberInsn(0.0);
    Assert.assertEquals(14, actual.getOpcode());
    
    actual = ASMUtils.getNumberInsn(3.0);
    Assert.assertEquals(18, actual.getOpcode());
  }

  @Test
  public void testGetIntegerFromInsn() throws Exception {
    Assert.assertEquals(1, ASMUtils.getIntegerFromInsn(new InsnNode(4)));
    Assert.assertEquals(1, ASMUtils.getIntegerFromInsn(new IntInsnNode(4, 1)));
    Assert.assertEquals(1, ASMUtils.getIntegerFromInsn(new LdcInsnNode(1)));

    thrown.expect(RadonException.class);
    ASMUtils.getIntegerFromInsn(new InsnNode(0));
  }

  @Test
  public void testGetLongFromInsn() throws Exception {
    Assert.assertEquals(0L, ASMUtils.getLongFromInsn(new InsnNode(9)));
    Assert.assertEquals(1L, ASMUtils.getLongFromInsn(new LdcInsnNode(1L)));

    thrown.expect(RadonException.class);
    ASMUtils.getLongFromInsn(new InsnNode(0));
  }

  @Test
  public void testGetFloatFromInsn() throws Exception {
    Assert.assertEquals(0.0f, ASMUtils.getFloatFromInsn(new InsnNode(11)), 0);
    Assert.assertEquals(1.0f, 
        ASMUtils.getFloatFromInsn(new LdcInsnNode(1.0f)), 0);

    thrown.expect(RadonException.class);
    ASMUtils.getFloatFromInsn(new InsnNode(0));
  }

  @Test
  public void testGetDoubleFromInsn() throws Exception {
    Assert.assertEquals(0.0, ASMUtils.getDoubleFromInsn(new InsnNode(14)), 0);
    Assert.assertEquals(1.0, 
        ASMUtils.getDoubleFromInsn(new LdcInsnNode(1.0)), 0);

    thrown.expect(RadonException.class);
    ASMUtils.getDoubleFromInsn(new InsnNode(0));
  }

  @Test
  public void testGetGenericMethodDesc() throws Exception {
    Assert.assertEquals("(ILjava/lang/Object;)[I",
      ASMUtils.getGenericMethodDesc("(ILjava/lang/Integer;)[I"));
  }
}
