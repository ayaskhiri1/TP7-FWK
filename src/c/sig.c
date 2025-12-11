#include <math.h>
#include <stdlib.h>
#include <jni.h>


double hyp(double a, double b) {
    return sqrt(a*a + b*b);
}
double avg(double *data, int n) {
    double s = 0;
    for(int i = 0; i < n; i++) {
        s += data[i];
    }
    return s / n;
}
double var(double *data, int n) {
    double m = avg(data, n);
    double s = 0;
    for(int i = 0; i < n; i++) {
        double d = data[i] - m;
        s += d * d;
    }
    return s / n;
}
void convolve(const double *signal, int n,
              const double *kernel, int k,
              double *out) {
    for (int i = 0; i < n; i++) {
        double s = 0;
        for (int j = 0; j < k; j++) {
            int idx = i - j;
            if (idx >= 0) {
                s += signal[idx] * kernel[j];
            }
        }
        out[i] = s;
    }
}

JNIEXPORT jdouble JNICALL Java_SigJNI_avg(JNIEnv *env, jclass cls, jdoubleArray arr) {
int n = (*env)->GetArrayLength(env, arr);
jdouble *ptr = (*env)->GetDoubleArrayElements(env, arr, 0);
double result = avg(ptr, n);
(*env)->ReleaseDoubleArrayElements(env, arr, ptr, 0);
return result;
}

JNIEXPORT jdoubleArray JNICALL Java_SigJNI_convolve(JNIEnv *env, jclass cls, jdoubleArray s, jdoubleArray k) {
int ns = (*env)->GetArrayLength(env, s);
int nk = (*env)->GetArrayLength(env, k);
jdouble *ps = (*env)->GetDoubleArrayElements(env, s, 0);
jdouble *pk = (*env)->GetDoubleArrayElements(env, k, 0);
jdoubleArray out = (*env)->NewDoubleArray(env, ns);
jdouble *po = malloc(sizeof(double)*ns);
convolve(ps, ns, pk, nk, po);
(*env)->SetDoubleArrayRegion(env, out, 0, ns, po);
free(po);
(*env)->ReleaseDoubleArrayElements(env, s, ps, 0);
(*env)->ReleaseDoubleArrayElements(env, k, pk, 0);
return out;
}
