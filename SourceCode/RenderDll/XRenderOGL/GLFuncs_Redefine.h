#ifndef GLFUNCS_REDEFINE_H__
#define GLFUNCS_REDEFINE_H__

// Automated mapping of OpenGL functions to CryEngine dynamically resolved function pointers
#ifndef glAccum
#define glAccum cryglAccum
#endif
#ifndef glAlphaFunc
#define glAlphaFunc cryglAlphaFunc
#endif
#ifndef glAreTexturesResident
#define glAreTexturesResident cryglAreTexturesResident
#endif
#ifndef glArrayElement
#define glArrayElement cryglArrayElement
#endif
#ifndef glBegin
#define glBegin cryglBegin
#endif
#ifndef glBindTexture
#define glBindTexture cryglBindTexture
#endif
#ifndef glBitmap
#define glBitmap cryglBitmap
#endif
#ifndef glBlendFunc
#define glBlendFunc cryglBlendFunc
#endif
#ifndef glCallList
#define glCallList cryglCallList
#endif
#ifndef glCallLists
#define glCallLists cryglCallLists
#endif
#ifndef glClear
#define glClear cryglClear
#endif
#ifndef glClearAccum
#define glClearAccum cryglClearAccum
#endif
#ifndef glClearColor
#define glClearColor cryglClearColor
#endif
#ifndef glClearDepth
#define glClearDepth cryglClearDepth
#endif
#ifndef glClearIndex
#define glClearIndex cryglClearIndex
#endif
#ifndef glClearStencil
#define glClearStencil cryglClearStencil
#endif
#ifndef glClipPlane
#define glClipPlane cryglClipPlane
#endif
#ifndef glColor3b
#define glColor3b cryglColor3b
#endif
#ifndef glColor3bv
#define glColor3bv cryglColor3bv
#endif
#ifndef glColor3d
#define glColor3d cryglColor3d
#endif
#ifndef glColor3dv
#define glColor3dv cryglColor3dv
#endif
#ifndef glColor3f
#define glColor3f cryglColor3f
#endif
#ifndef glColor3fv
#define glColor3fv cryglColor3fv
#endif
#ifndef glColor3i
#define glColor3i cryglColor3i
#endif
#ifndef glColor3iv
#define glColor3iv cryglColor3iv
#endif
#ifndef glColor3s
#define glColor3s cryglColor3s
#endif
#ifndef glColor3sv
#define glColor3sv cryglColor3sv
#endif
#ifndef glColor3ub
#define glColor3ub cryglColor3ub
#endif
#ifndef glColor3ubv
#define glColor3ubv cryglColor3ubv
#endif
#ifndef glColor3ui
#define glColor3ui cryglColor3ui
#endif
#ifndef glColor3uiv
#define glColor3uiv cryglColor3uiv
#endif
#ifndef glColor3us
#define glColor3us cryglColor3us
#endif
#ifndef glColor3usv
#define glColor3usv cryglColor3usv
#endif
#ifndef glColor4b
#define glColor4b cryglColor4b
#endif
#ifndef glColor4bv
#define glColor4bv cryglColor4bv
#endif
#ifndef glColor4d
#define glColor4d cryglColor4d
#endif
#ifndef glColor4dv
#define glColor4dv cryglColor4dv
#endif
#ifndef glColor4f
#define glColor4f cryglColor4f
#endif
#ifndef glColor4fv
#define glColor4fv cryglColor4fv
#endif
#ifndef glColor4i
#define glColor4i cryglColor4i
#endif
#ifndef glColor4iv
#define glColor4iv cryglColor4iv
#endif
#ifndef glColor4s
#define glColor4s cryglColor4s
#endif
#ifndef glColor4sv
#define glColor4sv cryglColor4sv
#endif
#ifndef glColor4ub
#define glColor4ub cryglColor4ub
#endif
#ifndef glColor4ubv
#define glColor4ubv cryglColor4ubv
#endif
#ifndef glColor4ui
#define glColor4ui cryglColor4ui
#endif
#ifndef glColor4uiv
#define glColor4uiv cryglColor4uiv
#endif
#ifndef glColor4us
#define glColor4us cryglColor4us
#endif
#ifndef glColor4usv
#define glColor4usv cryglColor4usv
#endif
#ifndef glColorMask
#define glColorMask cryglColorMask
#endif
#ifndef glColorMaterial
#define glColorMaterial cryglColorMaterial
#endif
#ifndef glColorPointer
#define glColorPointer cryglColorPointer
#endif
#ifndef glCopyPixels
#define glCopyPixels cryglCopyPixels
#endif
#ifndef glCopyTexImage1D
#define glCopyTexImage1D cryglCopyTexImage1D
#endif
#ifndef glCopyTexImage2D
#define glCopyTexImage2D cryglCopyTexImage2D
#endif
#ifndef glCopyTexSubImage1D
#define glCopyTexSubImage1D cryglCopyTexSubImage1D
#endif
#ifndef glCopyTexSubImage2D
#define glCopyTexSubImage2D cryglCopyTexSubImage2D
#endif
#ifndef glCullFace
#define glCullFace cryglCullFace
#endif
#ifndef glDeleteLists
#define glDeleteLists cryglDeleteLists
#endif
#ifndef glDeleteTextures
#define glDeleteTextures cryglDeleteTextures
#endif
#ifndef glDepthFunc
#define glDepthFunc cryglDepthFunc
#endif
#ifndef glDepthMask
#define glDepthMask cryglDepthMask
#endif
#ifndef glDepthRange
#define glDepthRange cryglDepthRange
#endif
#ifndef glDisable
#define glDisable cryglDisable
#endif
#ifndef glDisableClientState
#define glDisableClientState cryglDisableClientState
#endif
#ifndef glDrawArrays
#define glDrawArrays cryglDrawArrays
#endif
#ifndef glDrawBuffer
#define glDrawBuffer cryglDrawBuffer
#endif
#ifndef glDrawElements
#define glDrawElements cryglDrawElements
#endif
#ifndef glDrawPixels
#define glDrawPixels cryglDrawPixels
#endif
#ifndef glEdgeFlag
#define glEdgeFlag cryglEdgeFlag
#endif
#ifndef glEdgeFlagPointer
#define glEdgeFlagPointer cryglEdgeFlagPointer
#endif
#ifndef glEdgeFlagv
#define glEdgeFlagv cryglEdgeFlagv
#endif
#ifndef glEnable
#define glEnable cryglEnable
#endif
#ifndef glEnableClientState
#define glEnableClientState cryglEnableClientState
#endif
#ifndef glEnd
#define glEnd cryglEnd
#endif
#ifndef glEndList
#define glEndList cryglEndList
#endif
#ifndef glEvalCoord1d
#define glEvalCoord1d cryglEvalCoord1d
#endif
#ifndef glEvalCoord1dv
#define glEvalCoord1dv cryglEvalCoord1dv
#endif
#ifndef glEvalCoord1f
#define glEvalCoord1f cryglEvalCoord1f
#endif
#ifndef glEvalCoord1fv
#define glEvalCoord1fv cryglEvalCoord1fv
#endif
#ifndef glEvalCoord2d
#define glEvalCoord2d cryglEvalCoord2d
#endif
#ifndef glEvalCoord2dv
#define glEvalCoord2dv cryglEvalCoord2dv
#endif
#ifndef glEvalCoord2f
#define glEvalCoord2f cryglEvalCoord2f
#endif
#ifndef glEvalCoord2fv
#define glEvalCoord2fv cryglEvalCoord2fv
#endif
#ifndef glEvalMesh1
#define glEvalMesh1 cryglEvalMesh1
#endif
#ifndef glEvalMesh2
#define glEvalMesh2 cryglEvalMesh2
#endif
#ifndef glEvalPoint1
#define glEvalPoint1 cryglEvalPoint1
#endif
#ifndef glEvalPoint2
#define glEvalPoint2 cryglEvalPoint2
#endif
#ifndef glFeedbackBuffer
#define glFeedbackBuffer cryglFeedbackBuffer
#endif
#ifndef glFinish
#define glFinish cryglFinish
#endif
#ifndef glFlush
#define glFlush cryglFlush
#endif
#ifndef glFogf
#define glFogf cryglFogf
#endif
#ifndef glFogfv
#define glFogfv cryglFogfv
#endif
#ifndef glFogi
#define glFogi cryglFogi
#endif
#ifndef glFogiv
#define glFogiv cryglFogiv
#endif
#ifndef glFrontFace
#define glFrontFace cryglFrontFace
#endif
#ifndef glFrustum
#define glFrustum cryglFrustum
#endif
#ifndef glGenLists
#define glGenLists cryglGenLists
#endif
#ifndef glGenTextures
#define glGenTextures cryglGenTextures
#endif
#ifndef glGetBooleanv
#define glGetBooleanv cryglGetBooleanv
#endif
#ifndef glGetClipPlane
#define glGetClipPlane cryglGetClipPlane
#endif
#ifndef glGetDoublev
#define glGetDoublev cryglGetDoublev
#endif
#ifndef glGetError
#define glGetError cryglGetError
#endif
#ifndef glGetFloatv
#define glGetFloatv cryglGetFloatv
#endif
#ifndef glGetIntegerv
#define glGetIntegerv cryglGetIntegerv
#endif
#ifndef glGetLightfv
#define glGetLightfv cryglGetLightfv
#endif
#ifndef glGetLightiv
#define glGetLightiv cryglGetLightiv
#endif
#ifndef glGetMapdv
#define glGetMapdv cryglGetMapdv
#endif
#ifndef glGetMapfv
#define glGetMapfv cryglGetMapfv
#endif
#ifndef glGetMapiv
#define glGetMapiv cryglGetMapiv
#endif
#ifndef glGetMaterialfv
#define glGetMaterialfv cryglGetMaterialfv
#endif
#ifndef glGetMaterialiv
#define glGetMaterialiv cryglGetMaterialiv
#endif
#ifndef glGetPixelMapfv
#define glGetPixelMapfv cryglGetPixelMapfv
#endif
#ifndef glGetPixelMapuiv
#define glGetPixelMapuiv cryglGetPixelMapuiv
#endif
#ifndef glGetPixelMapusv
#define glGetPixelMapusv cryglGetPixelMapusv
#endif
#ifndef glGetPointerv
#define glGetPointerv cryglGetPointerv
#endif
#ifndef glGetPolygonStipple
#define glGetPolygonStipple cryglGetPolygonStipple
#endif
#ifndef glGetString
#define glGetString cryglGetString
#endif
#ifndef glGetTexEnvfv
#define glGetTexEnvfv cryglGetTexEnvfv
#endif
#ifndef glGetTexEnviv
#define glGetTexEnviv cryglGetTexEnviv
#endif
#ifndef glGetTexGendv
#define glGetTexGendv cryglGetTexGendv
#endif
#ifndef glGetTexGenfv
#define glGetTexGenfv cryglGetTexGenfv
#endif
#ifndef glGetTexGeniv
#define glGetTexGeniv cryglGetTexGeniv
#endif
#ifndef glGetTexImage
#define glGetTexImage cryglGetTexImage
#endif
#ifndef glGetTexLevelParameterfv
#define glGetTexLevelParameterfv cryglGetTexLevelParameterfv
#endif
#ifndef glGetTexLevelParameteriv
#define glGetTexLevelParameteriv cryglGetTexLevelParameteriv
#endif
#ifndef glGetTexParameterfv
#define glGetTexParameterfv cryglGetTexParameterfv
#endif
#ifndef glGetTexParameteriv
#define glGetTexParameteriv cryglGetTexParameteriv
#endif
#ifndef glHint
#define glHint cryglHint
#endif
#ifndef glIndexMask
#define glIndexMask cryglIndexMask
#endif
#ifndef glIndexPointer
#define glIndexPointer cryglIndexPointer
#endif
#ifndef glIndexd
#define glIndexd cryglIndexd
#endif
#ifndef glIndexdv
#define glIndexdv cryglIndexdv
#endif
#ifndef glIndexf
#define glIndexf cryglIndexf
#endif
#ifndef glIndexfv
#define glIndexfv cryglIndexfv
#endif
#ifndef glIndexi
#define glIndexi cryglIndexi
#endif
#ifndef glIndexiv
#define glIndexiv cryglIndexiv
#endif
#ifndef glIndexs
#define glIndexs cryglIndexs
#endif
#ifndef glIndexsv
#define glIndexsv cryglIndexsv
#endif
#ifndef glIndexub
#define glIndexub cryglIndexub
#endif
#ifndef glIndexubv
#define glIndexubv cryglIndexubv
#endif
#ifndef glInitNames
#define glInitNames cryglInitNames
#endif
#ifndef glInterleavedArrays
#define glInterleavedArrays cryglInterleavedArrays
#endif
#ifndef glIsEnabled
#define glIsEnabled cryglIsEnabled
#endif
#ifndef glIsList
#define glIsList cryglIsList
#endif
#ifndef glIsTexture
#define glIsTexture cryglIsTexture
#endif
#ifndef glLightModelf
#define glLightModelf cryglLightModelf
#endif
#ifndef glLightModelfv
#define glLightModelfv cryglLightModelfv
#endif
#ifndef glLightModeli
#define glLightModeli cryglLightModeli
#endif
#ifndef glLightModeliv
#define glLightModeliv cryglLightModeliv
#endif
#ifndef glLightf
#define glLightf cryglLightf
#endif
#ifndef glLightfv
#define glLightfv cryglLightfv
#endif
#ifndef glLighti
#define glLighti cryglLighti
#endif
#ifndef glLightiv
#define glLightiv cryglLightiv
#endif
#ifndef glLineStipple
#define glLineStipple cryglLineStipple
#endif
#ifndef glLineWidth
#define glLineWidth cryglLineWidth
#endif
#ifndef glListBase
#define glListBase cryglListBase
#endif
#ifndef glLoadIdentity
#define glLoadIdentity cryglLoadIdentity
#endif
#ifndef glLoadMatrixd
#define glLoadMatrixd cryglLoadMatrixd
#endif
#ifndef glLoadMatrixf
#define glLoadMatrixf cryglLoadMatrixf
#endif
#ifndef glLoadName
#define glLoadName cryglLoadName
#endif
#ifndef glLogicOp
#define glLogicOp cryglLogicOp
#endif
#ifndef glMap1d
#define glMap1d cryglMap1d
#endif
#ifndef glMap1f
#define glMap1f cryglMap1f
#endif
#ifndef glMap2d
#define glMap2d cryglMap2d
#endif
#ifndef glMap2f
#define glMap2f cryglMap2f
#endif
#ifndef glMapGrid1d
#define glMapGrid1d cryglMapGrid1d
#endif
#ifndef glMapGrid1f
#define glMapGrid1f cryglMapGrid1f
#endif
#ifndef glMapGrid2d
#define glMapGrid2d cryglMapGrid2d
#endif
#ifndef glMapGrid2f
#define glMapGrid2f cryglMapGrid2f
#endif
#ifndef glMaterialf
#define glMaterialf cryglMaterialf
#endif
#ifndef glMaterialfv
#define glMaterialfv cryglMaterialfv
#endif
#ifndef glMateriali
#define glMateriali cryglMateriali
#endif
#ifndef glMaterialiv
#define glMaterialiv cryglMaterialiv
#endif
#ifndef glMatrixMode
#define glMatrixMode cryglMatrixMode
#endif
#ifndef glMultMatrixd
#define glMultMatrixd cryglMultMatrixd
#endif
#ifndef glMultMatrixf
#define glMultMatrixf cryglMultMatrixf
#endif
#ifndef glNewList
#define glNewList cryglNewList
#endif
#ifndef glNormal3b
#define glNormal3b cryglNormal3b
#endif
#ifndef glNormal3bv
#define glNormal3bv cryglNormal3bv
#endif
#ifndef glNormal3d
#define glNormal3d cryglNormal3d
#endif
#ifndef glNormal3dv
#define glNormal3dv cryglNormal3dv
#endif
#ifndef glNormal3f
#define glNormal3f cryglNormal3f
#endif
#ifndef glNormal3fv
#define glNormal3fv cryglNormal3fv
#endif
#ifndef glNormal3i
#define glNormal3i cryglNormal3i
#endif
#ifndef glNormal3iv
#define glNormal3iv cryglNormal3iv
#endif
#ifndef glNormal3s
#define glNormal3s cryglNormal3s
#endif
#ifndef glNormal3sv
#define glNormal3sv cryglNormal3sv
#endif
#ifndef glNormalPointer
#define glNormalPointer cryglNormalPointer
#endif
#ifndef glOrtho
#define glOrtho cryglOrtho
#endif
#ifndef glPassThrough
#define glPassThrough cryglPassThrough
#endif
#ifndef glPixelMapfv
#define glPixelMapfv cryglPixelMapfv
#endif
#ifndef glPixelMapuiv
#define glPixelMapuiv cryglPixelMapuiv
#endif
#ifndef glPixelMapusv
#define glPixelMapusv cryglPixelMapusv
#endif
#ifndef glPixelStoref
#define glPixelStoref cryglPixelStoref
#endif
#ifndef glPixelStorei
#define glPixelStorei cryglPixelStorei
#endif
#ifndef glPixelTransferf
#define glPixelTransferf cryglPixelTransferf
#endif
#ifndef glPixelTransferi
#define glPixelTransferi cryglPixelTransferi
#endif
#ifndef glPixelZoom
#define glPixelZoom cryglPixelZoom
#endif
#ifndef glPointSize
#define glPointSize cryglPointSize
#endif
#ifndef glPolygonMode
#define glPolygonMode cryglPolygonMode
#endif
#ifndef glPolygonOffset
#define glPolygonOffset cryglPolygonOffset
#endif
#ifndef glPolygonStipple
#define glPolygonStipple cryglPolygonStipple
#endif
#ifndef glPopAttrib
#define glPopAttrib cryglPopAttrib
#endif
#ifndef glPopClientAttrib
#define glPopClientAttrib cryglPopClientAttrib
#endif
#ifndef glPopMatrix
#define glPopMatrix cryglPopMatrix
#endif
#ifndef glPopName
#define glPopName cryglPopName
#endif
#ifndef glPrioritizeTextures
#define glPrioritizeTextures cryglPrioritizeTextures
#endif
#ifndef glPushAttrib
#define glPushAttrib cryglPushAttrib
#endif
#ifndef glPushClientAttrib
#define glPushClientAttrib cryglPushClientAttrib
#endif
#ifndef glPushMatrix
#define glPushMatrix cryglPushMatrix
#endif
#ifndef glPushName
#define glPushName cryglPushName
#endif
#ifndef glRasterPos2d
#define glRasterPos2d cryglRasterPos2d
#endif
#ifndef glRasterPos2dv
#define glRasterPos2dv cryglRasterPos2dv
#endif
#ifndef glRasterPos2f
#define glRasterPos2f cryglRasterPos2f
#endif
#ifndef glRasterPos2fv
#define glRasterPos2fv cryglRasterPos2fv
#endif
#ifndef glRasterPos2i
#define glRasterPos2i cryglRasterPos2i
#endif
#ifndef glRasterPos2iv
#define glRasterPos2iv cryglRasterPos2iv
#endif
#ifndef glRasterPos2s
#define glRasterPos2s cryglRasterPos2s
#endif
#ifndef glRasterPos2sv
#define glRasterPos2sv cryglRasterPos2sv
#endif
#ifndef glRasterPos3d
#define glRasterPos3d cryglRasterPos3d
#endif
#ifndef glRasterPos3dv
#define glRasterPos3dv cryglRasterPos3dv
#endif
#ifndef glRasterPos3f
#define glRasterPos3f cryglRasterPos3f
#endif
#ifndef glRasterPos3fv
#define glRasterPos3fv cryglRasterPos3fv
#endif
#ifndef glRasterPos3i
#define glRasterPos3i cryglRasterPos3i
#endif
#ifndef glRasterPos3iv
#define glRasterPos3iv cryglRasterPos3iv
#endif
#ifndef glRasterPos3s
#define glRasterPos3s cryglRasterPos3s
#endif
#ifndef glRasterPos3sv
#define glRasterPos3sv cryglRasterPos3sv
#endif
#ifndef glRasterPos4d
#define glRasterPos4d cryglRasterPos4d
#endif
#ifndef glRasterPos4dv
#define glRasterPos4dv cryglRasterPos4dv
#endif
#ifndef glRasterPos4f
#define glRasterPos4f cryglRasterPos4f
#endif
#ifndef glRasterPos4fv
#define glRasterPos4fv cryglRasterPos4fv
#endif
#ifndef glRasterPos4i
#define glRasterPos4i cryglRasterPos4i
#endif
#ifndef glRasterPos4iv
#define glRasterPos4iv cryglRasterPos4iv
#endif
#ifndef glRasterPos4s
#define glRasterPos4s cryglRasterPos4s
#endif
#ifndef glRasterPos4sv
#define glRasterPos4sv cryglRasterPos4sv
#endif
#ifndef glReadBuffer
#define glReadBuffer cryglReadBuffer
#endif
#ifndef glReadPixels
#define glReadPixels cryglReadPixels
#endif
#ifndef glRectd
#define glRectd cryglRectd
#endif
#ifndef glRectdv
#define glRectdv cryglRectdv
#endif
#ifndef glRectf
#define glRectf cryglRectf
#endif
#ifndef glRectfv
#define glRectfv cryglRectfv
#endif
#ifndef glRecti
#define glRecti cryglRecti
#endif
#ifndef glRectiv
#define glRectiv cryglRectiv
#endif
#ifndef glRects
#define glRects cryglRects
#endif
#ifndef glRectsv
#define glRectsv cryglRectsv
#endif
#ifndef glRenderMode
#define glRenderMode cryglRenderMode
#endif
#ifndef glRotated
#define glRotated cryglRotated
#endif
#ifndef glRotatef
#define glRotatef cryglRotatef
#endif
#ifndef glScaled
#define glScaled cryglScaled
#endif
#ifndef glScalef
#define glScalef cryglScalef
#endif
#ifndef glScissor
#define glScissor cryglScissor
#endif
#ifndef glSelectBuffer
#define glSelectBuffer cryglSelectBuffer
#endif
#ifndef glShadeModel
#define glShadeModel cryglShadeModel
#endif
#ifndef glStencilFunc
#define glStencilFunc cryglStencilFunc
#endif
#ifndef glStencilMask
#define glStencilMask cryglStencilMask
#endif
#ifndef glStencilOp
#define glStencilOp cryglStencilOp
#endif
#ifndef glTexCoord1d
#define glTexCoord1d cryglTexCoord1d
#endif
#ifndef glTexCoord1dv
#define glTexCoord1dv cryglTexCoord1dv
#endif
#ifndef glTexCoord1f
#define glTexCoord1f cryglTexCoord1f
#endif
#ifndef glTexCoord1fv
#define glTexCoord1fv cryglTexCoord1fv
#endif
#ifndef glTexCoord1i
#define glTexCoord1i cryglTexCoord1i
#endif
#ifndef glTexCoord1iv
#define glTexCoord1iv cryglTexCoord1iv
#endif
#ifndef glTexCoord1s
#define glTexCoord1s cryglTexCoord1s
#endif
#ifndef glTexCoord1sv
#define glTexCoord1sv cryglTexCoord1sv
#endif
#ifndef glTexCoord2d
#define glTexCoord2d cryglTexCoord2d
#endif
#ifndef glTexCoord2dv
#define glTexCoord2dv cryglTexCoord2dv
#endif
#ifndef glTexCoord2f
#define glTexCoord2f cryglTexCoord2f
#endif
#ifndef glTexCoord2fv
#define glTexCoord2fv cryglTexCoord2fv
#endif
#ifndef glTexCoord2i
#define glTexCoord2i cryglTexCoord2i
#endif
#ifndef glTexCoord2iv
#define glTexCoord2iv cryglTexCoord2iv
#endif
#ifndef glTexCoord2s
#define glTexCoord2s cryglTexCoord2s
#endif
#ifndef glTexCoord2sv
#define glTexCoord2sv cryglTexCoord2sv
#endif
#ifndef glTexCoord3d
#define glTexCoord3d cryglTexCoord3d
#endif
#ifndef glTexCoord3dv
#define glTexCoord3dv cryglTexCoord3dv
#endif
#ifndef glTexCoord3f
#define glTexCoord3f cryglTexCoord3f
#endif
#ifndef glTexCoord3fv
#define glTexCoord3fv cryglTexCoord3fv
#endif
#ifndef glTexCoord3i
#define glTexCoord3i cryglTexCoord3i
#endif
#ifndef glTexCoord3iv
#define glTexCoord3iv cryglTexCoord3iv
#endif
#ifndef glTexCoord3s
#define glTexCoord3s cryglTexCoord3s
#endif
#ifndef glTexCoord3sv
#define glTexCoord3sv cryglTexCoord3sv
#endif
#ifndef glTexCoord4d
#define glTexCoord4d cryglTexCoord4d
#endif
#ifndef glTexCoord4dv
#define glTexCoord4dv cryglTexCoord4dv
#endif
#ifndef glTexCoord4f
#define glTexCoord4f cryglTexCoord4f
#endif
#ifndef glTexCoord4fv
#define glTexCoord4fv cryglTexCoord4fv
#endif
#ifndef glTexCoord4i
#define glTexCoord4i cryglTexCoord4i
#endif
#ifndef glTexCoord4iv
#define glTexCoord4iv cryglTexCoord4iv
#endif
#ifndef glTexCoord4s
#define glTexCoord4s cryglTexCoord4s
#endif
#ifndef glTexCoord4sv
#define glTexCoord4sv cryglTexCoord4sv
#endif
#ifndef glTexCoordPointer
#define glTexCoordPointer cryglTexCoordPointer
#endif
#ifndef glTexEnvf
#define glTexEnvf cryglTexEnvf
#endif
#ifndef glTexEnvfv
#define glTexEnvfv cryglTexEnvfv
#endif
#ifndef glTexEnvi
#define glTexEnvi cryglTexEnvi
#endif
#ifndef glTexEnviv
#define glTexEnviv cryglTexEnviv
#endif
#ifndef glTexGend
#define glTexGend cryglTexGend
#endif
#ifndef glTexGendv
#define glTexGendv cryglTexGendv
#endif
#ifndef glTexGenf
#define glTexGenf cryglTexGenf
#endif
#ifndef glTexGenfv
#define glTexGenfv cryglTexGenfv
#endif
#ifndef glTexGeni
#define glTexGeni cryglTexGeni
#endif
#ifndef glTexGeniv
#define glTexGeniv cryglTexGeniv
#endif
#ifndef glTexImage1D
#define glTexImage1D cryglTexImage1D
#endif
#ifndef glTexImage2D
#define glTexImage2D cryglTexImage2D
#endif
#ifndef glTexParameterf
#define glTexParameterf cryglTexParameterf
#endif
#ifndef glTexParameterfv
#define glTexParameterfv cryglTexParameterfv
#endif
#ifndef glTexParameteri
#define glTexParameteri cryglTexParameteri
#endif
#ifndef glTexParameteriv
#define glTexParameteriv cryglTexParameteriv
#endif
#ifndef glTexSubImage1D
#define glTexSubImage1D cryglTexSubImage1D
#endif
#ifndef glTexSubImage2D
#define glTexSubImage2D cryglTexSubImage2D
#endif
#ifndef glTranslated
#define glTranslated cryglTranslated
#endif
#ifndef glTranslatef
#define glTranslatef cryglTranslatef
#endif
#ifndef glVertex2d
#define glVertex2d cryglVertex2d
#endif
#ifndef glVertex2dv
#define glVertex2dv cryglVertex2dv
#endif
#ifndef glVertex2f
#define glVertex2f cryglVertex2f
#endif
#ifndef glVertex2fv
#define glVertex2fv cryglVertex2fv
#endif
#ifndef glVertex2i
#define glVertex2i cryglVertex2i
#endif
#ifndef glVertex2iv
#define glVertex2iv cryglVertex2iv
#endif
#ifndef glVertex2s
#define glVertex2s cryglVertex2s
#endif
#ifndef glVertex2sv
#define glVertex2sv cryglVertex2sv
#endif
#ifndef glVertex3d
#define glVertex3d cryglVertex3d
#endif
#ifndef glVertex3dv
#define glVertex3dv cryglVertex3dv
#endif
#ifndef glVertex3f
#define glVertex3f cryglVertex3f
#endif
#ifndef glVertex3fv
#define glVertex3fv cryglVertex3fv
#endif
#ifndef glVertex3i
#define glVertex3i cryglVertex3i
#endif
#ifndef glVertex3iv
#define glVertex3iv cryglVertex3iv
#endif
#ifndef glVertex3s
#define glVertex3s cryglVertex3s
#endif
#ifndef glVertex3sv
#define glVertex3sv cryglVertex3sv
#endif
#ifndef glVertex4d
#define glVertex4d cryglVertex4d
#endif
#ifndef glVertex4dv
#define glVertex4dv cryglVertex4dv
#endif
#ifndef glVertex4f
#define glVertex4f cryglVertex4f
#endif
#ifndef glVertex4fv
#define glVertex4fv cryglVertex4fv
#endif
#ifndef glVertex4i
#define glVertex4i cryglVertex4i
#endif
#ifndef glVertex4iv
#define glVertex4iv cryglVertex4iv
#endif
#ifndef glVertex4s
#define glVertex4s cryglVertex4s
#endif
#ifndef glVertex4sv
#define glVertex4sv cryglVertex4sv
#endif
#ifndef glVertexPointer
#define glVertexPointer cryglVertexPointer
#endif
#ifndef glViewport
#define glViewport cryglViewport
#endif
#ifndef glColorTableEXT
#define glColorTableEXT cryglColorTableEXT
#endif
#ifndef glColorSubTableEXT
#define glColorSubTableEXT cryglColorSubTableEXT
#endif
#ifndef glGetColorTableEXT
#define glGetColorTableEXT cryglGetColorTableEXT
#endif
#ifndef glGetColorTableParameterivEXT
#define glGetColorTableParameterivEXT cryglGetColorTableParameterivEXT
#endif
#ifndef glGetColorTableParameterfvEXT
#define glGetColorTableParameterfvEXT cryglGetColorTableParameterfvEXT
#endif
#ifndef glCompressedTexImage3DARB
#define glCompressedTexImage3DARB cryglCompressedTexImage3DARB
#endif
#ifndef glCompressedTexImage2DARB
#define glCompressedTexImage2DARB cryglCompressedTexImage2DARB
#endif
#ifndef glCompressedTexImage1DARB
#define glCompressedTexImage1DARB cryglCompressedTexImage1DARB
#endif
#ifndef glCompressedTexSubImage3DARB
#define glCompressedTexSubImage3DARB cryglCompressedTexSubImage3DARB
#endif
#ifndef glCompressedTexSubImage2DARB
#define glCompressedTexSubImage2DARB cryglCompressedTexSubImage2DARB
#endif
#ifndef glCompressedTexSubImage1DARB
#define glCompressedTexSubImage1DARB cryglCompressedTexSubImage1DARB
#endif
#ifndef glGetCompressedTexImageARB
#define glGetCompressedTexImageARB cryglGetCompressedTexImageARB
#endif
#ifndef glDepthBoundsEXT
#define glDepthBoundsEXT cryglDepthBoundsEXT
#endif
#ifndef glLockArraysEXT
#define glLockArraysEXT cryglLockArraysEXT
#endif
#ifndef glUnlockArraysEXT
#define glUnlockArraysEXT cryglUnlockArraysEXT
#endif
#ifndef wglSwapIntervalEXT
#define wglSwapIntervalEXT crywglSwapIntervalEXT
#endif
#ifndef wglGetPixelFormatAttribivARB
#define wglGetPixelFormatAttribivARB crywglGetPixelFormatAttribivARB
#endif
#ifndef wglGetPixelFormatAttribfvARB
#define wglGetPixelFormatAttribfvARB crywglGetPixelFormatAttribfvARB
#endif
#ifndef wglChoosePixelFormatARB
#define wglChoosePixelFormatARB crywglChoosePixelFormatARB
#endif
#ifndef wglBindTexImageARB
#define wglBindTexImageARB crywglBindTexImageARB
#endif
#ifndef wglReleaseTexImageARB
#define wglReleaseTexImageARB crywglReleaseTexImageARB
#endif
#ifndef wglMakeContextCurrentARB
#define wglMakeContextCurrentARB crywglMakeContextCurrentARB
#endif
#ifndef wglGetCurrentReadDCARB
#define wglGetCurrentReadDCARB crywglGetCurrentReadDCARB
#endif
#ifndef glSampleCoverageARB
#define glSampleCoverageARB cryglSampleCoverageARB
#endif
#ifndef wglCreatePbufferARB
#define wglCreatePbufferARB crywglCreatePbufferARB
#endif
#ifndef wglGetPbufferDCARB
#define wglGetPbufferDCARB crywglGetPbufferDCARB
#endif
#ifndef wglReleasePbufferDCARB
#define wglReleasePbufferDCARB crywglReleasePbufferDCARB
#endif
#ifndef wglDestroyPbufferARB
#define wglDestroyPbufferARB crywglDestroyPbufferARB
#endif
#ifndef wglQueryPbufferARB
#define wglQueryPbufferARB crywglQueryPbufferARB
#endif
#ifndef wglCreateBufferRegionARB
#define wglCreateBufferRegionARB crywglCreateBufferRegionARB
#endif
#ifndef wglDeleteBufferRegionARB
#define wglDeleteBufferRegionARB crywglDeleteBufferRegionARB
#endif
#ifndef wglSaveBufferRegionARB
#define wglSaveBufferRegionARB crywglSaveBufferRegionARB
#endif
#ifndef wglRestoreBufferRegionARB
#define wglRestoreBufferRegionARB crywglRestoreBufferRegionARB
#endif
#ifndef wglGetDeviceGammaRamp3DFX
#define wglGetDeviceGammaRamp3DFX crywglGetDeviceGammaRamp3DFX
#endif
#ifndef wglSetDeviceGammaRamp3DFX
#define wglSetDeviceGammaRamp3DFX crywglSetDeviceGammaRamp3DFX
#endif
#ifndef glMultiTexCoord1fARB
#define glMultiTexCoord1fARB cryglMultiTexCoord1fARB
#endif
#ifndef glMultiTexCoord2fARB
#define glMultiTexCoord2fARB cryglMultiTexCoord2fARB
#endif
#ifndef glMultiTexCoord3fARB
#define glMultiTexCoord3fARB cryglMultiTexCoord3fARB
#endif
#ifndef glMultiTexCoord4fARB
#define glMultiTexCoord4fARB cryglMultiTexCoord4fARB
#endif
#ifndef glMultiTexCoord1fvARB
#define glMultiTexCoord1fvARB cryglMultiTexCoord1fvARB
#endif
#ifndef glMultiTexCoord2fvARB
#define glMultiTexCoord2fvARB cryglMultiTexCoord2fvARB
#endif
#ifndef glMultiTexCoord3fvARB
#define glMultiTexCoord3fvARB cryglMultiTexCoord3fvARB
#endif
#ifndef glMultiTexCoord4fvARB
#define glMultiTexCoord4fvARB cryglMultiTexCoord4fvARB
#endif
#ifndef glActiveTextureARB
#define glActiveTextureARB cryglActiveTextureARB
#endif
#ifndef glClientActiveTextureARB
#define glClientActiveTextureARB cryglClientActiveTextureARB
#endif
#ifndef glPointParameterfEXT
#define glPointParameterfEXT cryglPointParameterfEXT
#endif
#ifndef glPointParameterfvEXT
#define glPointParameterfvEXT cryglPointParameterfvEXT
#endif
#ifndef glCullParameterdvSGI
#define glCullParameterdvSGI cryglCullParameterdvSGI
#endif
#ifndef glCullParameterfvSGI
#define glCullParameterfvSGI cryglCullParameterfvSGI
#endif
#ifndef glVertexArrayRangeNV
#define glVertexArrayRangeNV cryglVertexArrayRangeNV
#endif
#ifndef glFlushVertexArrayRangeNV
#define glFlushVertexArrayRangeNV cryglFlushVertexArrayRangeNV
#endif
#ifndef wglAllocateMemoryNV
#define wglAllocateMemoryNV crywglAllocateMemoryNV
#endif
#ifndef wglFreeMemoryNV
#define wglFreeMemoryNV crywglFreeMemoryNV
#endif
#ifndef glDeleteFencesNV
#define glDeleteFencesNV cryglDeleteFencesNV
#endif
#ifndef glGenFencesNV
#define glGenFencesNV cryglGenFencesNV
#endif
#ifndef glIsFenceNV
#define glIsFenceNV cryglIsFenceNV
#endif
#ifndef glTestFenceNV
#define glTestFenceNV cryglTestFenceNV
#endif
#ifndef glGetFenceivNV
#define glGetFenceivNV cryglGetFenceivNV
#endif
#ifndef glFinishFenceNV
#define glFinishFenceNV cryglFinishFenceNV
#endif
#ifndef glSetFenceNV
#define glSetFenceNV cryglSetFenceNV
#endif
#ifndef glSecondaryColor3bEXT
#define glSecondaryColor3bEXT cryglSecondaryColor3bEXT
#endif
#ifndef glSecondaryColor3bvEXT
#define glSecondaryColor3bvEXT cryglSecondaryColor3bvEXT
#endif
#ifndef glSecondaryColor3dEXT
#define glSecondaryColor3dEXT cryglSecondaryColor3dEXT
#endif
#ifndef glSecondaryColor3dvEXT
#define glSecondaryColor3dvEXT cryglSecondaryColor3dvEXT
#endif
#ifndef glSecondaryColor3fEXT
#define glSecondaryColor3fEXT cryglSecondaryColor3fEXT
#endif
#ifndef glSecondaryColor3fvEXT
#define glSecondaryColor3fvEXT cryglSecondaryColor3fvEXT
#endif
#ifndef glSecondaryColor3iEXT
#define glSecondaryColor3iEXT cryglSecondaryColor3iEXT
#endif
#ifndef glSecondaryColor3ivEXT
#define glSecondaryColor3ivEXT cryglSecondaryColor3ivEXT
#endif
#ifndef glSecondaryColor3sEXT
#define glSecondaryColor3sEXT cryglSecondaryColor3sEXT
#endif
#ifndef glSecondaryColor3svEXT
#define glSecondaryColor3svEXT cryglSecondaryColor3svEXT
#endif
#ifndef glSecondaryColor3ubEXT
#define glSecondaryColor3ubEXT cryglSecondaryColor3ubEXT
#endif
#ifndef glSecondaryColor3ubvEXT
#define glSecondaryColor3ubvEXT cryglSecondaryColor3ubvEXT
#endif
#ifndef glSecondaryColor3uiEXT
#define glSecondaryColor3uiEXT cryglSecondaryColor3uiEXT
#endif
#ifndef glSecondaryColor3uivEXT
#define glSecondaryColor3uivEXT cryglSecondaryColor3uivEXT
#endif
#ifndef glSecondaryColor3usEXT
#define glSecondaryColor3usEXT cryglSecondaryColor3usEXT
#endif
#ifndef glSecondaryColor3usvEXT
#define glSecondaryColor3usvEXT cryglSecondaryColor3usvEXT
#endif
#ifndef glSecondaryColorPointerEXT
#define glSecondaryColorPointerEXT cryglSecondaryColorPointerEXT
#endif
#ifndef glMultiDrawArraysEXT
#define glMultiDrawArraysEXT cryglMultiDrawArraysEXT
#endif
#ifndef glMultiDrawElementsEXT
#define glMultiDrawElementsEXT cryglMultiDrawElementsEXT
#endif
#ifndef glPointParameteriNV
#define glPointParameteriNV cryglPointParameteriNV
#endif
#ifndef glPointParameterivNV
#define glPointParameterivNV cryglPointParameterivNV
#endif
#ifndef glCombinerParameterfvNV
#define glCombinerParameterfvNV cryglCombinerParameterfvNV
#endif
#ifndef glCombinerParameterfNV
#define glCombinerParameterfNV cryglCombinerParameterfNV
#endif
#ifndef glCombinerParameterivNV
#define glCombinerParameterivNV cryglCombinerParameterivNV
#endif
#ifndef glCombinerParameteriNV
#define glCombinerParameteriNV cryglCombinerParameteriNV
#endif
#ifndef glCombinerInputNV
#define glCombinerInputNV cryglCombinerInputNV
#endif
#ifndef glCombinerOutputNV
#define glCombinerOutputNV cryglCombinerOutputNV
#endif
#ifndef glFinalCombinerInputNV
#define glFinalCombinerInputNV cryglFinalCombinerInputNV
#endif
#ifndef glGetCombinerInputParameterfvNV
#define glGetCombinerInputParameterfvNV cryglGetCombinerInputParameterfvNV
#endif
#ifndef glGetCombinerInputParameterivNV
#define glGetCombinerInputParameterivNV cryglGetCombinerInputParameterivNV
#endif
#ifndef glGetCombinerOutputParameterfvNV
#define glGetCombinerOutputParameterfvNV cryglGetCombinerOutputParameterfvNV
#endif
#ifndef glGetCombinerOutputParameterivNV
#define glGetCombinerOutputParameterivNV cryglGetCombinerOutputParameterivNV
#endif
#ifndef glGetFinalCombinerInputParameterfvNV
#define glGetFinalCombinerInputParameterfvNV cryglGetFinalCombinerInputParameterfvNV
#endif
#ifndef glGetFinalCombinerInputParameterivNV
#define glGetFinalCombinerInputParameterivNV cryglGetFinalCombinerInputParameterivNV
#endif
#ifndef glCombinerStageParameterfvNV
#define glCombinerStageParameterfvNV cryglCombinerStageParameterfvNV
#endif
#ifndef glGetCombinerStageParameterfvNV
#define glGetCombinerStageParameterfvNV cryglGetCombinerStageParameterfvNV
#endif
#ifndef glAreProgramsResidentNV
#define glAreProgramsResidentNV cryglAreProgramsResidentNV
#endif
#ifndef glBindProgramNV
#define glBindProgramNV cryglBindProgramNV
#endif
#ifndef glDeleteProgramsNV
#define glDeleteProgramsNV cryglDeleteProgramsNV
#endif
#ifndef glExecuteProgramNV
#define glExecuteProgramNV cryglExecuteProgramNV
#endif
#ifndef glGenProgramsNV
#define glGenProgramsNV cryglGenProgramsNV
#endif
#ifndef glGetProgramParameterdvNV
#define glGetProgramParameterdvNV cryglGetProgramParameterdvNV
#endif
#ifndef glGetProgramParameterfvNV
#define glGetProgramParameterfvNV cryglGetProgramParameterfvNV
#endif
#ifndef glGetProgramivNV
#define glGetProgramivNV cryglGetProgramivNV
#endif
#ifndef glGetProgramStringNV
#define glGetProgramStringNV cryglGetProgramStringNV
#endif
#ifndef glGetTrackMatrixivNV
#define glGetTrackMatrixivNV cryglGetTrackMatrixivNV
#endif
#ifndef glGetVertexAttribdvNV
#define glGetVertexAttribdvNV cryglGetVertexAttribdvNV
#endif
#ifndef glGetVertexAttribfvNV
#define glGetVertexAttribfvNV cryglGetVertexAttribfvNV
#endif
#ifndef glGetVertexAttribivNV
#define glGetVertexAttribivNV cryglGetVertexAttribivNV
#endif
#ifndef glIsProgramNV
#define glIsProgramNV cryglIsProgramNV
#endif
#ifndef glLoadProgramNV
#define glLoadProgramNV cryglLoadProgramNV
#endif
#ifndef glProgramParameter4dNV
#define glProgramParameter4dNV cryglProgramParameter4dNV
#endif
#ifndef glProgramParameter4dvNV
#define glProgramParameter4dvNV cryglProgramParameter4dvNV
#endif
#ifndef glProgramParameter4fNV
#define glProgramParameter4fNV cryglProgramParameter4fNV
#endif
#ifndef glProgramParameter4fvNV
#define glProgramParameter4fvNV cryglProgramParameter4fvNV
#endif
#ifndef glProgramParameters4dvNV
#define glProgramParameters4dvNV cryglProgramParameters4dvNV
#endif
#ifndef glProgramParameters4fvNV
#define glProgramParameters4fvNV cryglProgramParameters4fvNV
#endif
#ifndef glRequestResidentProgramsNV
#define glRequestResidentProgramsNV cryglRequestResidentProgramsNV
#endif
#ifndef glTrackMatrixNV
#define glTrackMatrixNV cryglTrackMatrixNV
#endif
#ifndef glVertexAttribPointerNV
#define glVertexAttribPointerNV cryglVertexAttribPointerNV
#endif
#ifndef glVertexAttrib1dNV
#define glVertexAttrib1dNV cryglVertexAttrib1dNV
#endif
#ifndef glVertexAttrib1dvNV
#define glVertexAttrib1dvNV cryglVertexAttrib1dvNV
#endif
#ifndef glVertexAttrib1fNV
#define glVertexAttrib1fNV cryglVertexAttrib1fNV
#endif
#ifndef glVertexAttrib1fvNV
#define glVertexAttrib1fvNV cryglVertexAttrib1fvNV
#endif
#ifndef glVertexAttrib1sNV
#define glVertexAttrib1sNV cryglVertexAttrib1sNV
#endif
#ifndef glVertexAttrib1svNV
#define glVertexAttrib1svNV cryglVertexAttrib1svNV
#endif
#ifndef glVertexAttrib2dNV
#define glVertexAttrib2dNV cryglVertexAttrib2dNV
#endif
#ifndef glVertexAttrib2dvNV
#define glVertexAttrib2dvNV cryglVertexAttrib2dvNV
#endif
#ifndef glVertexAttrib2fNV
#define glVertexAttrib2fNV cryglVertexAttrib2fNV
#endif
#ifndef glVertexAttrib2fvNV
#define glVertexAttrib2fvNV cryglVertexAttrib2fvNV
#endif
#ifndef glVertexAttrib2sNV
#define glVertexAttrib2sNV cryglVertexAttrib2sNV
#endif
#ifndef glVertexAttrib2svNV
#define glVertexAttrib2svNV cryglVertexAttrib2svNV
#endif
#ifndef glVertexAttrib3dNV
#define glVertexAttrib3dNV cryglVertexAttrib3dNV
#endif
#ifndef glVertexAttrib3dvNV
#define glVertexAttrib3dvNV cryglVertexAttrib3dvNV
#endif
#ifndef glVertexAttrib3fNV
#define glVertexAttrib3fNV cryglVertexAttrib3fNV
#endif
#ifndef glVertexAttrib3fvNV
#define glVertexAttrib3fvNV cryglVertexAttrib3fvNV
#endif
#ifndef glVertexAttrib3sNV
#define glVertexAttrib3sNV cryglVertexAttrib3sNV
#endif
#ifndef glVertexAttrib3svNV
#define glVertexAttrib3svNV cryglVertexAttrib3svNV
#endif
#ifndef glVertexAttrib4dNV
#define glVertexAttrib4dNV cryglVertexAttrib4dNV
#endif
#ifndef glVertexAttrib4dvNV
#define glVertexAttrib4dvNV cryglVertexAttrib4dvNV
#endif
#ifndef glVertexAttrib4fNV
#define glVertexAttrib4fNV cryglVertexAttrib4fNV
#endif
#ifndef glVertexAttrib4fvNV
#define glVertexAttrib4fvNV cryglVertexAttrib4fvNV
#endif
#ifndef glVertexAttrib4sNV
#define glVertexAttrib4sNV cryglVertexAttrib4sNV
#endif
#ifndef glVertexAttrib4svNV
#define glVertexAttrib4svNV cryglVertexAttrib4svNV
#endif
#ifndef glVertexAttrib4ubvNV
#define glVertexAttrib4ubvNV cryglVertexAttrib4ubvNV
#endif
#ifndef glVertexAttribs1dvNV
#define glVertexAttribs1dvNV cryglVertexAttribs1dvNV
#endif
#ifndef glVertexAttribs1fvNV
#define glVertexAttribs1fvNV cryglVertexAttribs1fvNV
#endif
#ifndef glVertexAttribs1svNV
#define glVertexAttribs1svNV cryglVertexAttribs1svNV
#endif
#ifndef glVertexAttribs2dvNV
#define glVertexAttribs2dvNV cryglVertexAttribs2dvNV
#endif
#ifndef glVertexAttribs2fvNV
#define glVertexAttribs2fvNV cryglVertexAttribs2fvNV
#endif
#ifndef glVertexAttribs2svNV
#define glVertexAttribs2svNV cryglVertexAttribs2svNV
#endif
#ifndef glVertexAttribs3dvNV
#define glVertexAttribs3dvNV cryglVertexAttribs3dvNV
#endif
#ifndef glVertexAttribs3fvNV
#define glVertexAttribs3fvNV cryglVertexAttribs3fvNV
#endif
#ifndef glVertexAttribs3svNV
#define glVertexAttribs3svNV cryglVertexAttribs3svNV
#endif
#ifndef glVertexAttribs4dvNV
#define glVertexAttribs4dvNV cryglVertexAttribs4dvNV
#endif
#ifndef glVertexAttribs4fvNV
#define glVertexAttribs4fvNV cryglVertexAttribs4fvNV
#endif
#ifndef glVertexAttribs4svNV
#define glVertexAttribs4svNV cryglVertexAttribs4svNV
#endif
#ifndef glVertexAttribs4ubvNV
#define glVertexAttribs4ubvNV cryglVertexAttribs4ubvNV
#endif
#ifndef glProgramNamedParameter4fNV
#define glProgramNamedParameter4fNV cryglProgramNamedParameter4fNV
#endif
#ifndef glProgramNamedParameter4dNV
#define glProgramNamedParameter4dNV cryglProgramNamedParameter4dNV
#endif
#ifndef glProgramNamedParameter4fvNV
#define glProgramNamedParameter4fvNV cryglProgramNamedParameter4fvNV
#endif
#ifndef glProgramNamedParameter4dvNV
#define glProgramNamedParameter4dvNV cryglProgramNamedParameter4dvNV
#endif
#ifndef glGetProgramNamedParameterfvNV
#define glGetProgramNamedParameterfvNV cryglGetProgramNamedParameterfvNV
#endif
#ifndef glGetProgramNamedParameterdvNV
#define glGetProgramNamedParameterdvNV cryglGetProgramNamedParameterdvNV
#endif
#ifndef glVertexAttrib1sARB
#define glVertexAttrib1sARB cryglVertexAttrib1sARB
#endif
#ifndef glVertexAttrib1fARB
#define glVertexAttrib1fARB cryglVertexAttrib1fARB
#endif
#ifndef glVertexAttrib1dARB
#define glVertexAttrib1dARB cryglVertexAttrib1dARB
#endif
#ifndef glVertexAttrib2sARB
#define glVertexAttrib2sARB cryglVertexAttrib2sARB
#endif
#ifndef glVertexAttrib2fARB
#define glVertexAttrib2fARB cryglVertexAttrib2fARB
#endif
#ifndef glVertexAttrib2dARB
#define glVertexAttrib2dARB cryglVertexAttrib2dARB
#endif
#ifndef glVertexAttrib3sARB
#define glVertexAttrib3sARB cryglVertexAttrib3sARB
#endif
#ifndef glVertexAttrib3fARB
#define glVertexAttrib3fARB cryglVertexAttrib3fARB
#endif
#ifndef glVertexAttrib3dARB
#define glVertexAttrib3dARB cryglVertexAttrib3dARB
#endif
#ifndef glVertexAttrib4sARB
#define glVertexAttrib4sARB cryglVertexAttrib4sARB
#endif
#ifndef glVertexAttrib4fARB
#define glVertexAttrib4fARB cryglVertexAttrib4fARB
#endif
#ifndef glVertexAttrib4dARB
#define glVertexAttrib4dARB cryglVertexAttrib4dARB
#endif
#ifndef glVertexAttrib4NubARB
#define glVertexAttrib4NubARB cryglVertexAttrib4NubARB
#endif
#ifndef glVertexAttrib1svARB
#define glVertexAttrib1svARB cryglVertexAttrib1svARB
#endif
#ifndef glVertexAttrib1fvARB
#define glVertexAttrib1fvARB cryglVertexAttrib1fvARB
#endif
#ifndef glVertexAttrib1dvARB
#define glVertexAttrib1dvARB cryglVertexAttrib1dvARB
#endif
#ifndef glVertexAttrib2svARB
#define glVertexAttrib2svARB cryglVertexAttrib2svARB
#endif
#ifndef glVertexAttrib2fvARB
#define glVertexAttrib2fvARB cryglVertexAttrib2fvARB
#endif
#ifndef glVertexAttrib2dvARB
#define glVertexAttrib2dvARB cryglVertexAttrib2dvARB
#endif
#ifndef glVertexAttrib3svARB
#define glVertexAttrib3svARB cryglVertexAttrib3svARB
#endif
#ifndef glVertexAttrib3fvARB
#define glVertexAttrib3fvARB cryglVertexAttrib3fvARB
#endif
#ifndef glVertexAttrib3dvARB
#define glVertexAttrib3dvARB cryglVertexAttrib3dvARB
#endif
#ifndef glVertexAttrib4bvARB
#define glVertexAttrib4bvARB cryglVertexAttrib4bvARB
#endif
#ifndef glVertexAttrib4svARB
#define glVertexAttrib4svARB cryglVertexAttrib4svARB
#endif
#ifndef glVertexAttrib4ivARB
#define glVertexAttrib4ivARB cryglVertexAttrib4ivARB
#endif
#ifndef glVertexAttrib4ubvARB
#define glVertexAttrib4ubvARB cryglVertexAttrib4ubvARB
#endif
#ifndef glVertexAttrib4usvARB
#define glVertexAttrib4usvARB cryglVertexAttrib4usvARB
#endif
#ifndef glVertexAttrib4uivARB
#define glVertexAttrib4uivARB cryglVertexAttrib4uivARB
#endif
#ifndef glVertexAttrib4fvARB
#define glVertexAttrib4fvARB cryglVertexAttrib4fvARB
#endif
#ifndef glVertexAttrib4dvARB
#define glVertexAttrib4dvARB cryglVertexAttrib4dvARB
#endif
#ifndef glVertexAttrib4NbvARB
#define glVertexAttrib4NbvARB cryglVertexAttrib4NbvARB
#endif
#ifndef glVertexAttrib4NsvARB
#define glVertexAttrib4NsvARB cryglVertexAttrib4NsvARB
#endif
#ifndef glVertexAttrib4NivARB
#define glVertexAttrib4NivARB cryglVertexAttrib4NivARB
#endif
#ifndef glVertexAttrib4NubvARB
#define glVertexAttrib4NubvARB cryglVertexAttrib4NubvARB
#endif
#ifndef glVertexAttrib4NusvARB
#define glVertexAttrib4NusvARB cryglVertexAttrib4NusvARB
#endif
#ifndef glVertexAttrib4NuivARB
#define glVertexAttrib4NuivARB cryglVertexAttrib4NuivARB
#endif
#ifndef glVertexAttribPointerARB
#define glVertexAttribPointerARB cryglVertexAttribPointerARB
#endif
#ifndef glEnableVertexAttribArrayARB
#define glEnableVertexAttribArrayARB cryglEnableVertexAttribArrayARB
#endif
#ifndef glDisableVertexAttribArrayARB
#define glDisableVertexAttribArrayARB cryglDisableVertexAttribArrayARB
#endif
#ifndef glProgramStringARB
#define glProgramStringARB cryglProgramStringARB
#endif
#ifndef glBindProgramARB
#define glBindProgramARB cryglBindProgramARB
#endif
#ifndef glDeleteProgramsARB
#define glDeleteProgramsARB cryglDeleteProgramsARB
#endif
#ifndef glGenProgramsARB
#define glGenProgramsARB cryglGenProgramsARB
#endif
#ifndef glProgramEnvParameter4dARB
#define glProgramEnvParameter4dARB cryglProgramEnvParameter4dARB
#endif
#ifndef glProgramEnvParameter4dvARB
#define glProgramEnvParameter4dvARB cryglProgramEnvParameter4dvARB
#endif
#ifndef glProgramEnvParameter4fARB
#define glProgramEnvParameter4fARB cryglProgramEnvParameter4fARB
#endif
#ifndef glProgramEnvParameter4fvARB
#define glProgramEnvParameter4fvARB cryglProgramEnvParameter4fvARB
#endif
#ifndef glProgramLocalParameter4dARB
#define glProgramLocalParameter4dARB cryglProgramLocalParameter4dARB
#endif
#ifndef glProgramLocalParameter4dvARB
#define glProgramLocalParameter4dvARB cryglProgramLocalParameter4dvARB
#endif
#ifndef glProgramLocalParameter4fARB
#define glProgramLocalParameter4fARB cryglProgramLocalParameter4fARB
#endif
#ifndef glProgramLocalParameter4fvARB
#define glProgramLocalParameter4fvARB cryglProgramLocalParameter4fvARB
#endif
#ifndef glGetProgramEnvParameterdvARB
#define glGetProgramEnvParameterdvARB cryglGetProgramEnvParameterdvARB
#endif
#ifndef glGetProgramEnvParameterfvARB
#define glGetProgramEnvParameterfvARB cryglGetProgramEnvParameterfvARB
#endif
#ifndef glGetProgramLocalParameterdvARB
#define glGetProgramLocalParameterdvARB cryglGetProgramLocalParameterdvARB
#endif
#ifndef glGetProgramLocalParameterfvARB
#define glGetProgramLocalParameterfvARB cryglGetProgramLocalParameterfvARB
#endif
#ifndef glGetProgramivARB
#define glGetProgramivARB cryglGetProgramivARB
#endif
#ifndef glGetProgramStringARB
#define glGetProgramStringARB cryglGetProgramStringARB
#endif
#ifndef glGetVertexAttribdvARB
#define glGetVertexAttribdvARB cryglGetVertexAttribdvARB
#endif
#ifndef glGetVertexAttribfvARB
#define glGetVertexAttribfvARB cryglGetVertexAttribfvARB
#endif
#ifndef glGetVertexAttribivARB
#define glGetVertexAttribivARB cryglGetVertexAttribivARB
#endif
#ifndef glGetVertexAttribPointervARB
#define glGetVertexAttribPointervARB cryglGetVertexAttribPointervARB
#endif
#ifndef glIsProgramARB
#define glIsProgramARB cryglIsProgramARB
#endif
#ifndef glBindBufferARB
#define glBindBufferARB cryglBindBufferARB
#endif
#ifndef glDeleteBuffersARB
#define glDeleteBuffersARB cryglDeleteBuffersARB
#endif
#ifndef glGenBuffersARB
#define glGenBuffersARB cryglGenBuffersARB
#endif
#ifndef glIsBufferARB
#define glIsBufferARB cryglIsBufferARB
#endif
#ifndef glBufferDataARB
#define glBufferDataARB cryglBufferDataARB
#endif
#ifndef glBufferSubDataARB
#define glBufferSubDataARB cryglBufferSubDataARB
#endif
#ifndef glGetBufferSubDataARB
#define glGetBufferSubDataARB cryglGetBufferSubDataARB
#endif
#ifndef glMapBufferARB
#define glMapBufferARB cryglMapBufferARB
#endif
#ifndef glUnmapBufferARB
#define glUnmapBufferARB cryglUnmapBufferARB
#endif
#ifndef glGetBufferParameterivARB
#define glGetBufferParameterivARB cryglGetBufferParameterivARB
#endif
#ifndef glGetBufferPointervARB
#define glGetBufferPointervARB cryglGetBufferPointervARB
#endif
#ifndef glTexImage3DEXT
#define glTexImage3DEXT cryglTexImage3DEXT
#endif
#ifndef glTexSubImage3DEXT
#define glTexSubImage3DEXT cryglTexSubImage3DEXT
#endif
#ifndef glFogCoordPointerEXT
#define glFogCoordPointerEXT cryglFogCoordPointerEXT
#endif
#ifndef glFogCoordfEXT
#define glFogCoordfEXT cryglFogCoordfEXT
#endif
#ifndef glDrawRangeElementsEXT
#define glDrawRangeElementsEXT cryglDrawRangeElementsEXT
#endif
#ifndef glGenOcclusionQueriesNV
#define glGenOcclusionQueriesNV cryglGenOcclusionQueriesNV
#endif
#ifndef glDeleteOcclusionQueriesNV
#define glDeleteOcclusionQueriesNV cryglDeleteOcclusionQueriesNV
#endif
#ifndef glIsOcclusionQueryNV
#define glIsOcclusionQueryNV cryglIsOcclusionQueryNV
#endif
#ifndef glBeginOcclusionQueryNV
#define glBeginOcclusionQueryNV cryglBeginOcclusionQueryNV
#endif
#ifndef glEndOcclusionQueryNV
#define glEndOcclusionQueryNV cryglEndOcclusionQueryNV
#endif
#ifndef glGetOcclusionQueryivNV
#define glGetOcclusionQueryivNV cryglGetOcclusionQueryivNV
#endif
#ifndef glGetOcclusionQueryuivNV
#define glGetOcclusionQueryuivNV cryglGetOcclusionQueryuivNV
#endif
#ifndef glStencilFuncSeparateATI
#define glStencilFuncSeparateATI cryglStencilFuncSeparateATI
#endif
#ifndef glStencilOpSeparateATI
#define glStencilOpSeparateATI cryglStencilOpSeparateATI
#endif
#ifndef glActiveStencilFaceEXT
#define glActiveStencilFaceEXT cryglActiveStencilFaceEXT
#endif
#ifndef glGenFragmentShadersATI
#define glGenFragmentShadersATI cryglGenFragmentShadersATI
#endif
#ifndef glBindFragmentShaderATI
#define glBindFragmentShaderATI cryglBindFragmentShaderATI
#endif
#ifndef glDeleteFragmentShaderATI
#define glDeleteFragmentShaderATI cryglDeleteFragmentShaderATI
#endif
#ifndef glBeginFragmentShaderATI
#define glBeginFragmentShaderATI cryglBeginFragmentShaderATI
#endif
#ifndef glEndFragmentShaderATI
#define glEndFragmentShaderATI cryglEndFragmentShaderATI
#endif
#ifndef glPassTexCoordATI
#define glPassTexCoordATI cryglPassTexCoordATI
#endif
#ifndef glSampleMapATI
#define glSampleMapATI cryglSampleMapATI
#endif
#ifndef glColorFragmentOp1ATI
#define glColorFragmentOp1ATI cryglColorFragmentOp1ATI
#endif
#ifndef glColorFragmentOp2ATI
#define glColorFragmentOp2ATI cryglColorFragmentOp2ATI
#endif
#ifndef glColorFragmentOp3ATI
#define glColorFragmentOp3ATI cryglColorFragmentOp3ATI
#endif
#ifndef glAlphaFragmentOp1ATI
#define glAlphaFragmentOp1ATI cryglAlphaFragmentOp1ATI
#endif
#ifndef glAlphaFragmentOp2ATI
#define glAlphaFragmentOp2ATI cryglAlphaFragmentOp2ATI
#endif
#ifndef glAlphaFragmentOp3ATI
#define glAlphaFragmentOp3ATI cryglAlphaFragmentOp3ATI
#endif
#ifndef glSetFragmentShaderConstantATI
#define glSetFragmentShaderConstantATI cryglSetFragmentShaderConstantATI
#endif

#endif // GLFUNCS_REDEFINE_H__
