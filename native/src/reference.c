
#include <string.h>

#include "jni.h"

#include "jvmti.h"
#include "util.h"
typedef struct objectReferrer ObjectReferrer;

typedef struct objectReferrerList ObjectReferrerList;

// 对象引用
struct objectReferrer {
	jlong count;// 引用数
	ObjectReferrer * next; // 下一个对象
};

// 对象引用列表，会按引用数量从小到大排序
struct objectReferrerList {
	int maxNum;// 最大数量，超过这个数量就不存储
	int num;// 当前链表长度
	ObjectReferrer * root; //根节点指针
};

// 把某个对象的引用数添加到排序链表中
void addObjectReferrer(ObjectReferrerList * list, jlong count) {
	// 从根元素开始依次遍历，从小到大排，找到合适的位置
	ObjectReferrer ** pBefore = &list->root;
	ObjectReferrer * after = 0;
	while (1) {
		ObjectReferrer * current = *pBefore;
		if (current == 0 ) {
			break;
		}
		if (count < current->count) {
			pBefore = &(current->next);
		} else {
			after = current;
			break;
		}
	}
	// 如果链表超过最大长度就舍弃掉根节点（最小）的元素。
	if (list->num >= list->maxNum) {

		if (pBefore == &list->root) {
			return;
		} else {
			ObjectReferrer * referrer = 0;
			referrer = (ObjectReferrer *) allocate(sizeof(ObjectReferrer));
			referrer->count = count;
			referrer->next = after;
			*pBefore = referrer;
			///
			list->root = list->root->next;
		}
	}
	// 如果没有超过最大长度就接到根节点的位置
	else {
		list->num++;
		ObjectReferrer * referrer = 0;
		referrer = (ObjectReferrer *) allocate(sizeof(ObjectReferrer));
		referrer->count = count;
		referrer->next = after;
		*pBefore = referrer;
	}
}

jint JNICALL iterate_ref_markReferrer(jvmtiHeapReferenceKind reference_kind,
		const jvmtiHeapReferenceInfo* reference_info, jlong class_tag,
		jlong referrer_class_tag, jlong size, jlong* tag_ptr,
		jlong* referrer_tag_ptr, jint length, void* user_data) {
	if (reference_kind != JVMTI_HEAP_REFERENCE_FIELD
			&& reference_kind != JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT) {
		return JVMTI_VISIT_OBJECTS;
	}
	if (referrer_tag_ptr != 0 && tag_ptr != 0 && *tag_ptr == 2
			&& referrer_tag_ptr != tag_ptr) {
		*referrer_tag_ptr = 1;
	}
	return JVMTI_VISIT_OBJECTS;
}

jint JNICALL  iterate_ref_markReferree(jvmtiHeapReferenceKind reference_kind,
		const jvmtiHeapReferenceInfo* reference_info, jlong class_tag,
		jlong referrer_class_tag, jlong size, jlong* tag_ptr,
		jlong* referrer_tag_ptr, jint length, void* user_data) {
	if (reference_kind != JVMTI_HEAP_REFERENCE_FIELD
			&& reference_kind != JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT) {
		return JVMTI_VISIT_OBJECTS;
	}
	if (referrer_tag_ptr != 0 && tag_ptr != 0 && *referrer_tag_ptr == 2
			&& referrer_tag_ptr != tag_ptr) {
		*tag_ptr = 1;
	}
	return JVMTI_VISIT_OBJECTS;
}

jint JNICALL  iterate_ref_markCountReference(
		jvmtiHeapReferenceKind reference_kind,
		const jvmtiHeapReferenceInfo* reference_info, jlong class_tag,
		jlong referrer_class_tag, jlong size, jlong* tag_ptr,
		jlong* referrer_tag_ptr, jint length, void* user_data) {
	// 只统计属性关系和数组元素关系，两类引用，其他类型忽略

	if (reference_kind != JVMTI_HEAP_REFERENCE_FIELD
			&& reference_kind != JVMTI_HEAP_REFERENCE_ARRAY_ELEMENT) {
		return JVMTI_VISIT_OBJECTS;
	}
	// 引用者的tag值-1
	if (referrer_tag_ptr != 0 && tag_ptr != 0 ) {
		(*referrer_tag_ptr)--;
	}
	return JVMTI_VISIT_OBJECTS;
}

jvmtiIterationControl JNICALL iterate_countReference(jlong class_tag,
		jlong size, jlong* tag_ptr, void* user_data) {
	// 如果该对象的引用数为负数，则把该引用数加入统计，并把该tag转为正数
	if (tag_ptr != 0 && *tag_ptr < 0) {
		ObjectReferrerList * list = (ObjectReferrerList *) user_data;
		addObjectReferrer(list, *tag_ptr);
		*tag_ptr = -(*tag_ptr);
	}
	return JVMTI_ITERATION_IGNORE;
}




JNIEXPORT void Java_com_chenjw_knife_agent_utils_NativeHelper_countReferree0(
		JNIEnv * env, jclass thisClass, jint maxNum, jlongArray countArray,
		jobjectArray objArray) {
	// 初始化jvmti
	initJvmti(env);
	// 遍历所有的引用，统计每个引用者的引用次数，引用次数存在该对象的tag中，为负数
	jvmtiHeapCallbacks * callbacks;
	callbacks = (jvmtiHeapCallbacks *) allocate(sizeof(jvmtiHeapCallbacks));
	callbacks->heap_reference_callback = (jvmtiHeapReferenceCallback)&iterate_ref_markCountReference;
	callbacks->primitive_field_callback = 0;
	callbacks->array_primitive_value_callback = 0;
	callbacks->string_primitive_value_callback = 0;
	(*jvmti)->FollowReferences(jvmti, 0, 0, 0, callbacks, 0 );
	// 遍历所有对象，把所有对象的引用数加入统计结果链表
	jint countObjts = 0;
	jobject * objs;
	jlong * tagResults;
	ObjectReferrerList * list = 0;
	list = (ObjectReferrerList *) allocate(sizeof(ObjectReferrerList));
	list->maxNum = maxNum;
	list->num = 0;
	list->root = 0;
	(*jvmti)->IterateOverHeap(jvmti, JVMTI_HEAP_OBJECT_TAGGED,
			iterate_countReference, list);
	// 遍历统计链表中引用数量最大的若干对象
	jlong* idToQuery;
	idToQuery = (jlong*) allocate(list->num * sizeof(jlong));
	ObjectReferrer* referrer = list->root;
	jint i = 0;
	while (referrer != 0 ) {
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
		}
		referrer = referrer->next;

	}
	// 根据引用数量去查找标记的对象
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
	// 释放已申请的内存
	deallocate(tagResults);
	deallocate(objs);
	deallocate(callbacks);
	deallocate(idToQuery);
	deallocate(list);
	// 把所有tag都恢复为0
	releaseTags();
}

JNIEXPORT jobjectArray  Java_com_chenjw_knife_agent_utils_NativeHelper_findReferrerByObject0(
		JNIEnv * env, jclass thisClass, jobject obj) {
	initJvmti(env);
	jclass loadedObject = (*env)->FindClass(env, "java/lang/Object");

	(*jvmti)->SetTag(jvmti, obj, 2);
	jvmtiHeapCallbacks callbacks;
	memset(&callbacks, 0, sizeof(callbacks));
	callbacks.heap_reference_callback = &iterate_ref_markReferrer;
	(*jvmti)->FollowReferences(jvmti, 0, 0, 0, &callbacks, 0 );
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
		(*env)->SetObjectArrayElement(env, arrayReturn, i, objs[i]);
	}
	deallocate(tagResults);
	deallocate(objs);
	releaseTags();
	return arrayReturn;
}

JNIEXPORT jobjectArray  Java_com_chenjw_knife_agent_utils_NativeHelper_findReferreeByObject0(
		JNIEnv * env, jclass thisClass, jobject obj) {

	initJvmti(env);
	jclass loadedObject = (*env)->FindClass(env, "java/lang/Object");

	(*jvmti)->SetTag(jvmti, obj, 2);

	jvmtiHeapCallbacks callbacks;

	memset(&callbacks, 0, sizeof(callbacks));

	callbacks.heap_reference_callback = &iterate_ref_markReferree;

	(*jvmti)->FollowReferences(jvmti, 0, 0, 0, &callbacks, 0 );
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
		(*env)->SetObjectArrayElement(env, arrayReturn, i, objs[i]);
	}
	deallocate(tagResults);
	deallocate(objs);
	releaseTags();
	return arrayReturn;
}
