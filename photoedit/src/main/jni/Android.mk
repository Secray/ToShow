LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := nativefilter
LOCAL_SRC_FILES =: include/NativeFilter.cpp
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := picwarp
LOCAL_SRC_FILES =: include/Picwarp.cpp
include $(BUILD_SHARED_LIBRARY)
