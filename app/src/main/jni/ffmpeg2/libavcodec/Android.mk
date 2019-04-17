LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

include $(LOCAL_PATH)/../av.mk

LOCAL_SRC_FILES := $(FFFILES)

LOCAL_C_INCLUDES :=		\
	$(LOCAL_PATH)		\
	$(LOCAL_PATH)/..

LOCAL_CFLAGS += $(FFCFLAGS)-Wa,-mimplict-it=thumb

LOCAL_LDLIBS := -lz -lpthread
LOCAL_STATIC_LIBRARIES := $(FFLIBS)

LOCAL_MODULE := $(FFNAME)

include $(BUILD_STATIC_LIBRARY)
