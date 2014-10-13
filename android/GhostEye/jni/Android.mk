LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE	:= cameranative
LOCAL_SRC_FILES	:= camera_native.c
LOCAL_LDLIBS	:= -llog -lGLESv2

include $(BUILD_SHARED_LIBRARY)