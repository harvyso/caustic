/*
 * This file is part of Caustic API.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic API is licensed under the Spout License Version 1.
 *
 * Caustic API is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic API is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.renderer.api;

import org.spout.math.imaginary.Quaternionf;
import org.spout.math.matrix.Matrix4f;
import org.spout.math.vector.Vector3f;

/**
 * Represents a camera with a projection, position and rotation, for rendering purposes.
 */
public class Camera {
    private static final Vector3f RIGHT = Vector3f.RIGHT;
    private static final Vector3f UP = Vector3f.UP;
    private static final Vector3f FORWARD = Vector3f.FORWARD.negate();
    private Matrix4f projection = new Matrix4f();
    private Vector3f position = new Vector3f(0, 0, 0);
    private Quaternionf rotation = new Quaternionf();
    private Matrix4f rotationMatrixInverse = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private boolean updateViewMatrix = true;

    /**
     * Creates a new camera from the supplied projection matrix.
     *
     * @param projection The projection matrix
     */
    public Camera(Matrix4f projection) {
        this.projection = projection;
    }

    /**
     * Returns the perspective projection matrix.
     *
     * @return The perspective projection matrix
     */
    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    /**
     * Sets the perspective projection matrix.
     *
     * @param projection The perspective projection matrix
     */
    public void setProjection(Matrix4f projection) {
        this.projection = projection;
    }

    /**
     * Returns the view matrix, which is the transformation matrix for the position and rotation.
     *
     * @return The view matrix
     */
    public Matrix4f getViewMatrix() {
        if (updateViewMatrix) {
            rotationMatrixInverse = Matrix4f.createRotation(rotation);
            final Matrix4f rotationMatrix = Matrix4f.createRotation(rotation.invert());
            final Matrix4f positionMatrix = Matrix4f.createTranslation(position.negate());
            viewMatrix = rotationMatrix.mul(positionMatrix);
            updateViewMatrix = false;
        }
        return viewMatrix;
    }

    /**
     * Gets the camera position.
     *
     * @return The camera position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the camera position.
     *
     * @param position The camera position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
        updateViewMatrix = true;
    }

    /**
     * Gets the camera rotation.
     *
     * @return The camera rotation
     */
    public Quaternionf getRotation() {
        return rotation;
    }

    /**
     * Sets the camera rotation.
     *
     * @param rotation The camera rotation
     */
    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
        updateViewMatrix = true;
    }

    /**
     * Gets the vector representing the right direction for the camera.
     *
     * @return The camera's right direction vector
     */
    public Vector3f getRight() {
        return toCamera(RIGHT);
    }

    /**
     * Gets the vector representing the up direction for the camera.
     *
     * @return The camera's up direction vector
     */
    public Vector3f getUp() {
        return toCamera(UP);
    }

    /**
     * Gets the vector representing the forward direction for the camera.
     *
     * @return The camera's forward direction vector
     */
    public Vector3f getForward() {
        return toCamera(FORWARD);
    }

    private Vector3f toCamera(Vector3f v) {
        if (rotationMatrixInverse != null) {
            return rotationMatrixInverse.transform(v.toVector4(1)).toVector3();
        }
        return v;
    }

    /**
     * Creates a new camera with a standard perspective projection matrix.
     *
     * @param fieldOfView The field of view, in degrees
     * @param windowWidth The window width
     * @param windowHeight The widow height
     * @param near The near plane
     * @param far The far plane
     * @return The camera
     */
    public static Camera createPerspective(float fieldOfView, int windowWidth, int windowHeight, float near, float far) {
        return new Camera(Matrix4f.createPerspective(fieldOfView, (float) windowWidth / windowHeight, near, far));
    }

    /**
     * Creates a new camera with a standard orthographic projection matrix.
     *
     * @param right the right most plane
     * @param left the left most plane
     * @param top the top plane
     * @param bottom the bottom plane
     * @param near the near plane
     * @param far the far plane
     * @return The camera
     */
    public static Camera createOrthographic(float right, float left, float top, float bottom, float near, float far) {
        return new Camera(Matrix4f.createOrthographic(right, left, top, bottom, near, far));
    }
}