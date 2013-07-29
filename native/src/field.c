
#include <string.h>

#include "jni.h"

#include "jvmti.h"
#include "util.h"

jobject getBooleanField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jboolean fieldValue = (*env)->GetBooleanField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Boolean");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(Z)V"), fieldValue);
	return r;
}

jobject getByteField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jbyte fieldValue = (*env)->GetByteField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Byte");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(B)V"), fieldValue);
	return r;
}

jobject getCharField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jchar fieldValue = (*env)->GetCharField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Character");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(C)V"), fieldValue);
	return r;
}

jobject getShortField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jshort fieldValue = (*env)->GetShortField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Short");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(S)V"), fieldValue);
	return r;
}

jobject getIntField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jint fieldValue = (*env)->GetIntField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Integer");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(I)V"), fieldValue);
	return r;
}

jobject getLongField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jlong fieldValue = (*env)->GetLongField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Long");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(J)V"), fieldValue);
	return r;
}

jobject getFloatField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jfloat fieldValue = (*env)->GetFloatField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Float");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(F)V"), fieldValue);
	return r;
}

jobject getDoubleField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jdouble fieldValue = (*env)->GetDoubleField(env, obj, fieldId);
	jclass clazz = (*env)->FindClass(env, "java/lang/Double");
	jobject r = (*env)->NewObject(env, clazz,
			(*env)->GetMethodID(env, clazz, "<init>", "(D)V"), fieldValue);
	return r;
}

jobject getObjectField(JNIEnv * env, jobject obj, jfieldID fieldId) {
	jobject fieldValue = (*env)->GetObjectField(env, obj, fieldId);
	return fieldValue;
}

jobject getFieldValue(JNIEnv * env, jobject obj, jclass fieldClass,
		jfieldID fieldId) {
	char* signature;
	(*jvmti)->GetClassSignature(jvmti, fieldClass, &signature, 0 );
	if (strcmp(signature, "Z") == 0) {
		return getBooleanField(env, obj, fieldId);
	} else if (strcmp(signature, "B") == 0) {
		return getByteField(env, obj, fieldId);
	} else if (strcmp(signature, "C") == 0) {
		return getCharField(env, obj, fieldId);
	} else if (strcmp(signature, "S") == 0) {
		return getShortField(env, obj, fieldId);
	} else if (strcmp(signature, "I") == 0) {
		return getIntField(env, obj, fieldId);
	} else if (strcmp(signature, "J") == 0) {
		return getLongField(env, obj, fieldId);
	} else if (strcmp(signature, "F") == 0) {
		return getFloatField(env, obj, fieldId);
	} else if (strcmp(signature, "D") == 0) {
		return getDoubleField(env, obj, fieldId);
	} else {
		return getObjectField(env, obj, fieldId);
	}
}

/////////////////////////////////////////////////
// get field
/////////////////////////////////////////////////

JNIEXPORT jobject  Java_com_chenjw_knife_agent_utils_NativeHelper_getFieldValue0(
		JNIEnv * env, jclass thisClass, jobject obj, jclass fieldClass,
		jstring fieldName, jclass fieldType) {

	initJvmti(env);
	char* fieldNameChars = (char*) (*env)->GetStringUTFChars(env, fieldName, 0);
	jclass klass = fieldClass;
	jint count = 0;
	jfieldID* fieldIds;
	(*jvmti)->GetClassFields(jvmti, klass, &count, &fieldIds);
	jint i;
	for (i = 0; i < count; i++) {
		char* tFieldName;
		(*jvmti)->GetFieldName(jvmti, klass, fieldIds[i], &tFieldName, 0, 0);
		if (strcmp(tFieldName, fieldNameChars) == 0) {
			jobject result = getFieldValue(env, obj, fieldType, fieldIds[i]);
			deallocate(tFieldName);
			deallocate(fieldIds);
			(*env)->ReleaseStringUTFChars(env, fieldName, fieldNameChars);
			return result;
		}
	}
	throwException(env, 0, "field not found");
	deallocate(fieldIds);
	(*env)->ReleaseStringUTFChars(env, fieldName, fieldNameChars);
	return 0 ;
}


/////////////////////////////////////////////////
// set field
/////////////////////////////////////////////////

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setObjectFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jobject newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetObjectField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setBooleanFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jboolean newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetBooleanField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setByteFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jbyte newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetByteField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setCharFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jchar newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetCharField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setShortFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jshort newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetShortField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setIntFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jint newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetIntField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setLongFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jlong newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetLongField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setFloatFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jfloat newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetFloatField(env, obj, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setDoubleFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field,
		jdouble newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetDoubleField(env, obj, fieldId, newValue);
}

/////////////////////////////////////////////////
// set static field
/////////////////////////////////////////////////

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticObjectFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jobject newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticObjectField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticBooleanFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jboolean newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticBooleanField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticByteFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jbyte newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticByteField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticCharFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jchar newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticCharField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticShortFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jshort newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticShortField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticIntFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jint newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticIntField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticLongFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jlong newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticLongField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticFloatFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jfloat newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticFloatField(env, clazz, fieldId, newValue);
}

JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_setStaticDoubleFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field,
		jdouble newValue) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	(*env)->SetStaticDoubleField(env, clazz, fieldId, newValue);
}


/////////////////////////////////////////////////
// get field
/////////////////////////////////////////////////

JNIEXPORT jobject  Java_com_chenjw_knife_agent_utils_NativeHelper_getObjectFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetObjectField(env, obj, fieldId);
}

JNIEXPORT jboolean  Java_com_chenjw_knife_agent_utils_NativeHelper_getBooleanFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetBooleanField(env, obj, fieldId);
}

JNIEXPORT jbyte  Java_com_chenjw_knife_agent_utils_NativeHelper_getByteFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetByteField(env, obj, fieldId);
}

JNIEXPORT jchar  Java_com_chenjw_knife_agent_utils_NativeHelper_getCharFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetCharField(env, obj, fieldId);
}

JNIEXPORT jshort  Java_com_chenjw_knife_agent_utils_NativeHelper_getShortFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetShortField(env, obj, fieldId);
}

JNIEXPORT jint  Java_com_chenjw_knife_agent_utils_NativeHelper_getIntFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetIntField(env, obj, fieldId);
}

JNIEXPORT jlong  Java_com_chenjw_knife_agent_utils_NativeHelper_getLongFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetLongField(env, obj, fieldId);
}

JNIEXPORT jfloat  Java_com_chenjw_knife_agent_utils_NativeHelper_getFloatFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetFloatField(env, obj, fieldId);
}

JNIEXPORT jdouble  Java_com_chenjw_knife_agent_utils_NativeHelper_getDoubleFieldValue0(
		JNIEnv *env, jclass thisClass, jobject obj, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetDoubleField(env, obj, fieldId);
}

/////////////////////////////////////////////////
// get static field
/////////////////////////////////////////////////

JNIEXPORT jobject  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticObjectFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticObjectField(env, clazz, fieldId);
}

JNIEXPORT jboolean  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticBooleanFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticBooleanField(env, clazz, fieldId);
}

JNIEXPORT jbyte  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticByteFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticByteField(env, clazz, fieldId);
}

JNIEXPORT jchar  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticCharFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticCharField(env, clazz, fieldId);
}

JNIEXPORT jshort  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticShortFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticShortField(env, clazz, fieldId);
}

JNIEXPORT jint  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticIntFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticIntField(env, clazz, fieldId);
}

JNIEXPORT jlong  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticLongFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticLongField(env, clazz, fieldId);
}

JNIEXPORT jfloat  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticFloatFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticFloatField(env, clazz, fieldId);
}

JNIEXPORT jdouble  Java_com_chenjw_knife_agent_utils_NativeHelper_getStaticDoubleFieldValue0(
		JNIEnv *env, jclass thisClass, jclass clazz, jobject field) {
	jfieldID fieldId = (*env)->FromReflectedField(env, field);
	return (*env)->GetStaticDoubleField(env, clazz, fieldId);
}
