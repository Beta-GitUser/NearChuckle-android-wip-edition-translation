/*=============================================================================
  GLUCompat.cpp : Standalone implementation of GLU functions for platforms
                  without libGLU (e.g. Android).
  Copyright (c) 2026 NearChuckle Far Cry Port
=============================================================================*/

#include "RenderPCH.h"
#include "GL_Renderer.h"
#include <math.h>

#ifndef M_PI
#define M_PI 3.14159265358979323846
#endif

extern "C" {

void APIENTRY gluPerspective(GLdouble fovy, GLdouble aspect, GLdouble zNear, GLdouble zFar)
{
    GLdouble xmin, xmax, ymin, ymax;
    ymax = zNear * tan(fovy * M_PI / 360.0);
    ymin = -ymax;
    xmin = ymin * aspect;
    xmax = ymax * aspect;
    glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
}

void APIENTRY gluLookAt(GLdouble eyex, GLdouble eyey, GLdouble eyez,
                        GLdouble centerx, GLdouble centery, GLdouble centerz,
                        GLdouble upx, GLdouble upy, GLdouble upz)
{
    float m[16];
    SGLFuncs::gluLookAt((float)eyex, (float)eyey, (float)eyez,
                        (float)centerx, (float)centery, (float)centerz,
                        (float)upx, (float)upy, (float)upz, m);
    glMultMatrixf(m);
}

const GLubyte* APIENTRY gluErrorString(GLenum errCode)
{
    switch (errCode) {
        case GL_NO_ERROR: return (const GLubyte*)"no error";
        case GL_INVALID_ENUM: return (const GLubyte*)"invalid enumerant";
        case GL_INVALID_VALUE: return (const GLubyte*)"invalid value";
        case GL_INVALID_OPERATION: return (const GLubyte*)"invalid operation";
        case GL_STACK_OVERFLOW: return (const GLubyte*)"stack overflow";
        case GL_STACK_UNDERFLOW: return (const GLubyte*)"stack underflow";
        case GL_OUT_OF_MEMORY: return (const GLubyte*)"out of memory";
        default: return (const GLubyte*)"unknown error";
    }
}

const GLubyte* APIENTRY gluGetString(GLenum name)
{
    (void)name;
    return (const GLubyte*)"1.3 FarCry-GLU";
}

void APIENTRY gluOrtho2D(GLdouble left, GLdouble right, GLdouble bottom, GLdouble top)
{
    glOrtho(left, right, bottom, top, -1.0, 1.0);
}

int APIENTRY gluProject(GLdouble objx, GLdouble objy, GLdouble objz,
                        const GLdouble model[16], const GLdouble proj[16],
                        const GLint viewport[4],
                        GLdouble* winx, GLdouble* winy, GLdouble* winz)
{
    float m[16], p[16], wx = 0.0f, wy = 0.0f, wz = 0.0f;
    for (int i = 0; i < 16; ++i) { m[i] = (float)model[i]; p[i] = (float)proj[i]; }
    int res = SGLFuncs::gluProject((float)objx, (float)objy, (float)objz, m, p, viewport, &wx, &wy, &wz);
    if (winx) *winx = (GLdouble)wx;
    if (winy) *winy = (GLdouble)wy;
    if (winz) *winz = (GLdouble)wz;
    return res;
}

int APIENTRY gluUnProject(GLdouble winx, GLdouble winy, GLdouble winz,
                          const GLdouble model[16], const GLdouble proj[16],
                          const GLint viewport[4],
                          GLdouble* objx, GLdouble* objy, GLdouble* objz)
{
    float m[16], p[16], ox = 0.0f, oy = 0.0f, oz = 0.0f;
    for (int i = 0; i < 16; ++i) { m[i] = (float)model[i]; p[i] = (float)proj[i]; }
    int res = SGLFuncs::gluUnProject((float)winx, (float)winy, (float)winz, m, p, viewport, &ox, &oy, &oz);
    if (objx) *objx = (GLdouble)ox;
    if (objy) *objy = (GLdouble)oy;
    if (objz) *objz = (GLdouble)oz;
    return res;
}

// Standalone quadrics implementation for CGLRenderer::DrawBall
static GLUquadric s_gluQuadricInstance;

GLUquadric* APIENTRY gluNewQuadric(void)
{
    return &s_gluQuadricInstance;
}

void APIENTRY gluDeleteQuadric(GLUquadric* state)
{
    (void)state;
}

void APIENTRY gluSphere(GLUquadric* q, GLdouble radius, GLint slices, GLint stacks)
{
    (void)q;
    if (slices <= 0 || stacks <= 0 || radius <= 0.0)
        return;
    for (int i = 0; i < stacks; ++i) {
        float lat0 = (float)M_PI * (-0.5f + (float)i / (float)stacks);
        float z0 = (float)radius * sinf(lat0);
        float zr0 = (float)radius * cosf(lat0);

        float lat1 = (float)M_PI * (-0.5f + (float)(i + 1) / (float)stacks);
        float z1 = (float)radius * sinf(lat1);
        float zr1 = (float)radius * cosf(lat1);

        glBegin(GL_QUAD_STRIP);
        for (int j = 0; j <= slices; ++j) {
            float lng = 2.0f * (float)M_PI * (float)j / (float)slices;
            float x = cosf(lng);
            float y = sinf(lng);
            glNormal3f(x * zr0 / (float)radius, y * zr0 / (float)radius, z0 / (float)radius);
            glVertex3f(x * zr0, y * zr0, z0);
            glNormal3f(x * zr1 / (float)radius, y * zr1 / (float)radius, z1 / (float)radius);
            glVertex3f(x * zr1, y * zr1, z1);
        }
        glEnd();
    }
}

} // extern "C"
