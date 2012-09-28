
#include <string.h>

#include "jni.h"

#include "jvmti.h"
#include "api.h"
#include "util.h"
typedef struct objectReferrer ObjectReferrer;

typedef struct objectReferrerList ObjectReferrerList;

struct objectReferrer {
	jlong count;
	jlong* tagPtr;
	ObjectReferrer * next;
};

struct objectReferrerList {
	int maxNum;
	int num;
	ObjectReferrer * root;
};
void addObjectReferrer(ObjectReferrerList * list, jlong count) {
	ObjectReferrer ** pBefore = &list->root;
	ObjectReferrer * after = NULL;

	while (1) {
		ObjectReferrer * current = *pBefore;

		if (current == NULL ) {
			break;
		}
		if (count < current->count) {
			pBefore = &(current->next);
		} else {
			after = current;
			break;
		}
	}
	if (list->num >= list->maxNum) {

		if (pBefore == &list->root) {
			return;
		} else {
			ObjectReferrer * referrer = NULL;
			referrer = (ObjectReferrer *) allocate(sizeof(ObjectReferrer));
			referrer->count = count;
			referrer->next = after;
			*pBefore = referrer;
			///
			list->root = list->root->next;
		}
	} else {
		list->num++;
		ObjectReferrer * referrer = NULL;
		referrer = (ObjectReferrer *) allocate(sizeof(ObjectReferrer));
		referrer->count = count;
		referrer->next = after;
		*pBefore = referrer;
	}
}

jint  iterate_ref_markReferrer(jvmtiHeapReferenceKind reference_kind,
		const jvmtiHeapReferenceInfo* reference_info, jlong class_tag,
		jlong referrer_class_tag, jlong size, jlong* tag_ptr,
		jlong* referrer_tag_ptr, jint length, void* user_data) {
	if (reference_kind != JVMTI_HEAP_REFERENCE_FIELD
			&& reference_kind != JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT) {
		return JVMTI_VISIT_OBJECTS;
	}
	if (referrer_tag_ptr != NULL && tag_ptr != NULL && *tag_ptr == 2
			&& referrer_tag_ptr != tag_ptr) {
		*referrer_tag_ptr = 1;
	}
	return JVMTI_VISIT_OBJECTS;
}

jint  iterate_ref_markReferree(jvmtiHeapReferenceKind reference_kind,
		const jvmtiHeapReferenceInfo* reference_info, jlong class_tag,
		jlong referrer_class_tag, jlong size, jlong* tag_ptr,
		jlong* referrer_tag_ptr, jint length, void* user_data) {
	if (reference_kind != JVMTI_HEAP_REFERENCE_FIELD
			&& reference_kind != JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT) {
		return JVMTI_VISIT_OBJECTS;
	}
	if (referrer_tag_ptr != NULL && tag_ptr != NULL && *referrer_tag_ptr == 2
			&& referrer_tag_ptr != tag_ptr) {
		*tag_ptr = 1;
	}
	return JVMTI_VISIT_OBJECTS;
}

jint iterate_ref_markCountReference(
		jvmtiHeapReferenceKind reference_kind,
		const jvmtiHeapReferenceInfo* reference_info, jlong class_tag,
		jlong referrer_class_tag, jlong size, jlong* tag_ptr,
		jlong* referrer_tag_ptr, jint length, void* user_data) {
	if (reference_kind != JVMTI_HEAP_REFERENCE_FIELD
			&& reference_kind != JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT) {
		return JVMTI_VISIT_OBJECTS;
	}
	if (referrer_tag_ptr != NULL && tag_ptr != NULL ) {
		(*referrer_tag_ptr)--;
		//printf("ccc%d\n",*referrer_tag_ptr);
	}
	return JVMTI_VISIT_OBJECTS;
}

jvmtiIterationControl iterate_countReference(jlong class_tag,
		jlong size, jlong* tag_ptr, void* user_data) {

	//printf("bbbb\n");
	if (tag_ptr != NULL && *tag_ptr < 0) {
		ObjectReferrerList * list = (ObjectReferrerList *) user_data;
		//printf("before tagPtr=%d,count=%d\n",tag_ptr,*tag_ptr);
		addObjectReferrer(list, *tag_ptr);
		*tag_ptr = -(*tag_ptr);
		//printf("after tagPtr=%d,count=%d\n",tag_ptr,*tag_ptr);
	}
	return JVMTI_ITERATION_IGNORE;
}




 void Java_com_chenjw_knife_agent_utils_NativeHelper_countReferree0(
		JNIEnv * env, jclass thisClass, jint maxNum, jlongArray countArray,
		jobjectArray objArray) {

	initJvmti(env);

	jvmtiHeapCallbacks * callbacks;
	callbacks = (jvmtiHeapCallbacks *) allocate(sizeof(jvmtiHeapCallbacks));
	callbacks->heap_reference_callback = &iterate_ref_markCountReference;
	callbacks->primitive_field_callback = NULL;
	callbacks->array_primitive_value_callback = NULL;
	callbacks->string_primitive_value_callback = NULL;
	(*jvmti)->FollowReferences(jvmti, 0, NULL, NULL, callbacks, NULL );
	jint countObjts = 0;
	jobject * objs;
	jlong * tagResults;
//printf("~~~1\n");
	ObjectReferrerList * list = NULL;
	list = (ObjectReferrerList *) allocate(sizeof(ObjectReferrerList));
	list->maxNum = maxNum;
	list->num = 0;
	list->root = NULL;
	(*jvmti)->IterateOverHeap(jvmti, JVMTI_HEAP_OBJECT_TAGGED,
			iterate_countReference, list);
	jlong* idToQuery;
//printf("~~~2\n");
	idToQuery = (jlong*) allocate(list->num * sizeof(jlong));
	ObjectReferrer* referrer = list->root;
	jint i = 0;
	while (referrer != NULL ) {
		jlong count = -(referrer->count);
		jint exist = 0;
		jint j;
		for (j = 0; j < i; j++) {
			if (idToQuery[j] == count) {
				exist = 1;
				break;
			}
		}
		if (!exist) {
			idToQuery[i] = count;
			i++;
			//printf("~~~%d\n",count);
		}
		referrer = referrer->next;

	}
//printf("~~~3\n");
	(*jvmti)->GetObjectsWithTags(jvmti, list->num, idToQuery, &countObjts,
			&objs, &tagResults);
	// Set the object array
	for (i = 0; i < maxNum; i++) {
		jint n = -1;
		jlong nn = -1;
		jint j;
		for (j = 0; j < countObjts; j++) {
			if (tagResults[j] > nn) {
				n = j;
				nn = tagResults[j];
			}
		}
		(*env)->SetLongArrayRegion(env, countArray, i, 1, &tagResults[n]);
		(*env)->SetObjectArrayElement(env, objArray, i, objs[n]);

		tagResults[n] = -1;

	}

	deallocate(tagResults);
	deallocate(objs);
	deallocate(callbacks);
	deallocate(idToQuery);
	deallocate(list);
	releaseTags();
}

 jobjectArray  Java_com_chenjw_knife_agent_utils_NativeHelper_findReferrerByObject0(
		JNIEnv * env, jclass thisClass, jobject obj) {
	initJvmti(env);
	jclass loadedObject = (*env)->FindClass(env, "java/lang/Object");

	(*jvmti)->SetTag(jvmti, obj, 2);
	jvmtiHeapCallbacks callbacks;
	memset(&callbacks, 0, sizeof(callbacks));
	callbacks.heap_reference_callback = &iterate_ref_markReferrer;
	(*jvmti)->FollowReferences(jvmti, 0, NULL, NULL, &callbacks, NULL );
	jint countObjts = 0;
	jobject * objs;
	jlong * tagResults;
	jlong idToQuery = 1;

	(*jvmti)->GetObjectsWithTags(jvmti, 1, &idToQuery, &countObjts, &objs,
			&tagResults);
	// Set the object array
	jobjectArray arrayReturn = (*env)->NewObjectArray(env, countObjts,
			loadedObject, 0);
	jint i;
	for (i = 0; i < countObjts; i++) {
		//printf("~~~%d\n",&objs[i]);
		(*env)->SetObjectArrayElement(env, arrayReturn, i, objs[i]);
	}
	deallocate(tagResults);
	deallocate(objs);
	releaseTags();
	return arrayReturn;
}

 jobjectArray  Java_com_chenjw_knife_agent_utils_NativeHelper_findReferreeByObject0(
		JNIEnv * env, jclass thisClass, jobject obj) {

	initJvmti(env);
	jclass loadedObject = (*env)->FindClass(env, "java/lang/Object");

	(*jvmti)->SetTag(jvmti, obj, 2);

	jvmtiHeapCallbacks callbacks;

	memset(&callbacks, 0, sizeof(callbacks));

	callbacks.heap_reference_callback = &iterate_ref_markReferree;

	(*jvmti)->FollowReferences(jvmti, 0, NULL, NULL, &callbacks, NULL );
	jint countObjts = 0;
	jobject * objs;
	jlong * tagResults;
	jlong idToQuery = 1;

	(*jvmti)->GetObjectsWithTags(jvmti, 1, &idToQuery, &countObjts, &objs,
			&tagResults);
	// Set the object array
	jobjectArray arrayReturn = (*env)->NewObjectArray(env, countObjts,
			loadedObject, 0);
	jint i;
	for (i = 0; i < countObjts; i++) {
		//printf("~~~%d\n",&objs[i]);
		(*env)->SetObjectArrayElement(env, arrayReturn, i, objs[i]);
	}
	deallocate(tagResults);
	deallocate(objs);
	releaseTags();
	return arrayReturn;
}
