import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

// code from exercise sheets

public class Mesh {
  
  private float[] vertices;
  private int[] indices;
  private int vertexStride = 8;
  private int vertexXYZFloats = 3;
  private int vertexNormalFloats = 3;
  private int vertexTexFloats = 2;
  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];
  
  public Mesh(GL3 gl, float[] vertices, int[] indices) {
    this.vertices = vertices;
    this.indices = indices;
    fillBuffers(gl);
  }
  
  public void render(GL3 gl) {
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  private void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
  
    // new code
    // Vertex normals (attribute 1)
    offset = vertexXYZFloats * Float.BYTES;
    gl.glVertexAttribPointer(1, vertexNormalFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
    gl.glEnableVertexAttribArray(1);

    // Texture coordinates (attribute 2)
    offset = (vertexXYZFloats + vertexNormalFloats) * Float.BYTES;
    gl.glVertexAttribPointer(2, vertexTexFloats, GL.GL_FLOAT, false, stride * Float.BYTES, offset);
    gl.glEnableVertexAttribArray(2);
    
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
    //gl.glBindVertexArray(0);
  }
  
  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

  //new code
  public float[] getVertices() {
    return vertices.clone(); 
}

  public int[] getIndices() {
      return indices.clone(); 
  }

  // Texture repetition
  public void setTextureScale(GL3 gl, float scaleX, float scaleY) {
    int texCoordOffset = vertexXYZFloats + vertexNormalFloats;
    for (int i = 0; i < vertices.length / vertexStride; i++) {
        int index = i * vertexStride + texCoordOffset;
        vertices[index] *= scaleX;     // Scale U-coordinate
        vertices[index + 1] *= scaleY; // Scale V-coordinate
    }
    // Re-upload the updated vertices buffer
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
  }
  
}