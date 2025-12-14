#include <math.h>
#include <stdlib.h>

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

double median(double *data, int n) {
    // Copie pour ne pas modifier l'original
    double *copy = malloc(sizeof(double) * n);
    for(int i = 0; i < n; i++) {
        copy[i] = data[i];
    }

    for(int i = 0; i < n-1; i++) {
        for(int j = 0; j < n-i-1; j++) {
            if(copy[j] > copy[j+1]) {
                double temp = copy[j];
                copy[j] = copy[j+1];
                copy[j+1] = temp;
            }
        }
    }

    double result;
    if(n % 2 == 0) {
        result = (copy[n/2 - 1] + copy[n/2]) / 2.0;
    } else {
        result = copy[n/2];
    }

    free(copy);
    return result;
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

void moving_average(const double *signal, int n,
                    int window_size,
                    double *out) {
    for (int i = 0; i < n; i++) {
        double sum = 0;
        int count = 0;

        for (int j = 0; j < window_size; j++) {
            int idx = i - j;
            if (idx >= 0) {
                sum += signal[idx];
                count++;
            }
        }

        out[i] = sum / count;
    }
}

double distance(double x1, double y1, double x2, double y2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    return sqrt(dx*dx + dy*dy);
}


double angle(double x1, double y1, double x2, double y2) {
    return atan2(y2, x2) - atan2(y1, x1);
}


double dot_product(double *v1, double *v2, int n) {
    double result = 0;
    for(int i = 0; i < n; i++) {
        result += v1[i] * v2[i];
    }
    return result;
}