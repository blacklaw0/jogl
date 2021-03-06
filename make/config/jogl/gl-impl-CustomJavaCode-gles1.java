public GLES1Impl(GLProfile glp, GLContextImpl context) {
  this._context = context; 
  if(null != context) {
      this.bufferSizeTracker  = context.getBufferSizeTracker();
      this.bufferStateTracker = context.getBufferStateTracker();
      this.glStateTracker     = context.getGLStateTracker();
  } else {
      this.bufferSizeTracker  = null;
      this.bufferStateTracker = null;
      this.glStateTracker     = null;
  }
  this.glProfile = glp;
}

@Override
public final boolean isGL4bc() {
    return false;
}

@Override
public final boolean isGL4() {
    return false;
}

@Override
public final boolean isGL3bc() {
    return false;
}

@Override
public final boolean isGL3() {
    return false;
}

@Override
public final boolean isGL2() {
    return false;
}

@Override
public final boolean isGLES1() {
    return true;
}

@Override
public final boolean isGLES2() {
    return false;
}

@Override
public final boolean isGLES() {
    return true;
}

@Override
public final boolean isGL2ES1() {
    return true;
}

@Override
public final boolean isGL2ES2() {
    return false;
}

@Override
public final boolean isGLES2Compatible() {
    return false;
}

@Override
public final boolean isGL2GL3() {
    return false;
}

@Override
public final boolean hasGLSL() {
    return false;
}

@Override
public boolean isNPOTTextureAvailable() {
  return false;
}

@Override
public final GL4bc getGL4bc() throws GLException {
    throw new GLException("Not a GL4bc implementation");
}

@Override
public final GL4 getGL4() throws GLException {
    throw new GLException("Not a GL4 implementation");
}

@Override
public final GL3bc getGL3bc() throws GLException {
    throw new GLException("Not a GL3bc implementation");
}

@Override
public final GL3 getGL3() throws GLException {
    throw new GLException("Not a GL3 implementation");
}

@Override
public final GL2 getGL2() throws GLException {
    throw new GLException("Not a GL2 implementation");
}

@Override
public final GLES1 getGLES1() throws GLException {
    return this;
}

@Override
public final GLES2 getGLES2() throws GLException {
    throw new GLException("Not a GLES2 implementation");
}

@Override
public final GL2ES1 getGL2ES1() throws GLException {
    return this;
}

@Override
public final GL2ES2 getGL2ES2() throws GLException {
    throw new GLException("Not a GL2ES2 implementation");
}

@Override
public final GL2GL3 getGL2GL3() throws GLException {
    throw new GLException("Not a GL2GL3 implementation");
}

//
// Helpers for ensuring the correct amount of texture data
//

private final GLBufferSizeTracker  bufferSizeTracker;
private final GLBufferStateTracker bufferStateTracker;
private final GLStateTracker       glStateTracker;

private final boolean checkBufferObject(boolean enabled,
                                        int state,
                                        String kind, boolean throwException) {
  final int buffer = bufferStateTracker.getBoundBufferObject(state, this);
  if (enabled) {
    if (0 == buffer) {
      if(throwException) {
          throw new GLException(kind + " must be enabled to call this method");
      }
      return false;
    }
  } else {
    if (0 != buffer) {
      if(throwException) {
          throw new GLException(kind + " must be disabled to call this method");
      }
      return false;
    }
  }
  return true;
}  

private final boolean checkArrayVBODisabled(boolean throwException) { 
  return checkBufferObject(false, // enabled
                           GL.GL_ARRAY_BUFFER,
                           "array vertex_buffer_object", throwException);
}

private final boolean checkArrayVBOEnabled(boolean throwException) { 
  return checkBufferObject(true, // enabled
                           GL.GL_ARRAY_BUFFER,
                           "array vertex_buffer_object", throwException);
}

private final boolean checkElementVBODisabled(boolean throwException) { 
  return checkBufferObject(false, // enabled
                           GL.GL_ELEMENT_ARRAY_BUFFER,
                           "element vertex_buffer_object", throwException);
}

private final boolean checkElementVBOEnabled(boolean throwException) { 
  return checkBufferObject(true, // enabled
                           GL.GL_ELEMENT_ARRAY_BUFFER,
                           "element vertex_buffer_object", throwException);
}

private final boolean checkUnpackPBODisabled(boolean throwException) { 
    // PBO n/a for ES 1.1 or ES 2.0
    return true;
}

private final boolean checkUnpackPBOEnabled(boolean throwException) { 
    // PBO n/a for ES 1.1 or ES 2.0
    return false;
}

private final boolean checkPackPBODisabled(boolean throwException) { 
    // PBO n/a for ES 1.1 or ES 2.0
    return true;
}

private final boolean checkPackPBOEnabled(boolean throwException) { 
    // PBO n/a for ES 1.1 or ES 2.0
    return false;
}

private final HashMap<MemoryObject, MemoryObject> arbMemCache = new HashMap<MemoryObject, MemoryObject>();

/** Entry point to C language function: <br> <code> LPVOID glMapBuffer(GLenum target, GLenum access); </code>    */
public final java.nio.ByteBuffer glMapBuffer(int target, int access) {
  final long __addr_ = ((GLES1ProcAddressTable)_context.getGLProcAddressTable())._addressof_glMapBuffer;
  if (__addr_ == 0) {
    throw new GLException("Method \"glMapBuffer\" not available");
  }
  final long sz = bufferSizeTracker.getBufferSize(bufferStateTracker, target, this);
  if (0 == sz) {
    return null;
  }
  final long addr = dispatch_glMapBuffer(target, access, __addr_);
  if (0 == addr) {
    return null;
  }
  ByteBuffer buffer;
  MemoryObject memObj0 = new MemoryObject(addr, sz); // object and key
  MemoryObject memObj1 = MemoryObject.getOrAddSafe(arbMemCache, memObj0);
  if(memObj0 == memObj1) {
    // just added ..
    if(null != memObj0.getBuffer()) {
        throw new InternalError();
    }
    buffer = newDirectByteBuffer(addr, sz);
    Buffers.nativeOrder(buffer);
    memObj0.setBuffer(buffer);
  } else {
    // already mapped
    buffer = memObj1.getBuffer();
    if(null == buffer) {
        throw new InternalError();
    }
  }
  buffer.position(0);
  return buffer;
}

/** Encapsulates function pointer for OpenGL function <br>: <code> LPVOID glMapBuffer(GLenum target, GLenum access); </code>    */
native private long dispatch_glMapBuffer(int target, int access, long glProcAddress);

native private ByteBuffer newDirectByteBuffer(long addr, long capacity);

@Override
public final void glVertexPointer(GLArrayData array) {
  if(array.getComponentCount()==0) return;
  if(array.isVBO()) {
      glVertexPointer(array.getComponentCount(), array.getComponentType(), array.getStride(), array.getVBOOffset());
  } else {
      glVertexPointer(array.getComponentCount(), array.getComponentType(), array.getStride(), array.getBuffer());
  }
}
@Override
public final void glColorPointer(GLArrayData array) {
  if(array.getComponentCount()==0) return;
  if(array.isVBO()) {
      glColorPointer(array.getComponentCount(), array.getComponentType(), array.getStride(), array.getVBOOffset());
  } else {
      glColorPointer(array.getComponentCount(), array.getComponentType(), array.getStride(), array.getBuffer());
  }

}
@Override
public final void glNormalPointer(GLArrayData array) {
  if(array.getComponentCount()==0) return;
  if(array.getComponentCount()!=3) {
    throw new GLException("Only 3 components per normal allowed");
  }
  if(array.isVBO()) {
      glNormalPointer(array.getComponentType(), array.getStride(), array.getVBOOffset());
  } else {
      glNormalPointer(array.getComponentType(), array.getStride(), array.getBuffer());
  }
}
@Override
public final void glTexCoordPointer(GLArrayData array) {
  if(array.getComponentCount()==0) return;
  if(array.isVBO()) {
      glTexCoordPointer(array.getComponentCount(), array.getComponentType(), array.getStride(), array.getVBOOffset());
  } else {
      glTexCoordPointer(array.getComponentCount(), array.getComponentType(), array.getStride(), array.getBuffer());
  }
}

