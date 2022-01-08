/*
OpenCL RandomForestClassifier
classifier_class_name = ProbabilityMapper
feature_specification = original gaussian_blur=1 difference_of_gaussian=1 laplace_box_of_gaussian_blur=1
num_ground_truth_dimensions = 2
num_classes = 3
num_features = 8
max_depth = 2
num_trees = 10
*/
__kernel void predict (IMAGE_in0_TYPE in0, IMAGE_in1_TYPE in1, IMAGE_in2_TYPE in2, IMAGE_in3_TYPE in3, IMAGE_in4_TYPE in4, IMAGE_in5_TYPE in5, IMAGE_in6_TYPE in6, IMAGE_in7_TYPE in7, IMAGE_out_TYPE out) {
 sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;
 const int x = get_global_id(0);
 const int y = get_global_id(1);
 const int z = get_global_id(2);
 float i0 = READ_IMAGE(in0, sampler, POS_in0_INSTANCE(x,y,z,0)).x;
 float i1 = READ_IMAGE(in1, sampler, POS_in1_INSTANCE(x,y,z,0)).x;
 float i2 = READ_IMAGE(in2, sampler, POS_in2_INSTANCE(x,y,z,0)).x;
 float i3 = READ_IMAGE(in3, sampler, POS_in3_INSTANCE(x,y,z,0)).x;
 float i4 = READ_IMAGE(in4, sampler, POS_in4_INSTANCE(x,y,z,0)).x;
 float i5 = READ_IMAGE(in5, sampler, POS_in5_INSTANCE(x,y,z,0)).x;
 float i6 = READ_IMAGE(in6, sampler, POS_in6_INSTANCE(x,y,z,0)).x;
 float i7 = READ_IMAGE(in7, sampler, POS_in7_INSTANCE(x,y,z,0)).x;
 float s0=0;
 float s1=0;
 float s2=0;
if(i4<68.0){
 if(i7<-27.97952651977539){
  s1+=3.0;
 } else {
  s0+=202.0;
  s1+=7.0;
 }
} else {
 if(i0<164.0){
  s0+=3.0;
  s1+=100.0;
  s2+=10.0;
 } else {
  s1+=14.0;
  s2+=181.0;
 }
}
if(i2<1.1869735717773438){
 if(i3<-9.662591934204102){
  s0+=74.0;
  s1+=26.0;
  s2+=13.0;
 } else {
  s0+=135.0;
  s1+=32.0;
  s2+=173.0;
 }
} else {
 if(i0<180.0){
  s1+=45.0;
 } else {
  s1+=2.0;
  s2+=20.0;
 }
}
if(i5<62.082252502441406){
 if(i7<-18.18817901611328){
  s0+=27.0;
  s1+=2.0;
 } else {
  s0+=182.0;
 }
} else {
 if(i4<164.0){
  s0+=5.0;
  s1+=103.0;
  s2+=10.0;
 } else {
  s1+=20.0;
  s2+=171.0;
 }
}
if(i0<60.0){
 if(i3<-27.819202423095703){
  s1+=2.0;
 } else {
  s0+=207.0;
  s1+=3.0;
 }
} else {
 if(i0<164.0){
  s0+=7.0;
  s1+=94.0;
  s2+=7.0;
 } else {
  s1+=13.0;
  s2+=187.0;
 }
}
if(i5<62.082252502441406){
 if(i3<-16.89200782775879){
  s0+=22.0;
  s1+=5.0;
 } else {
  s0+=189.0;
 }
} else {
 if(i1<158.913330078125){
  s0+=6.0;
  s1+=98.0;
  s2+=2.0;
 } else {
  s1+=7.0;
  s2+=191.0;
 }
}
if(i1<68.943115234375){
 if(i5<53.34342575073242){
  s0+=214.0;
  s1+=4.0;
 } else {
  s0+=14.0;
  s1+=6.0;
 }
} else {
 if(i4<164.0){
  s0+=3.0;
  s1+=92.0;
  s2+=5.0;
 } else {
  s1+=11.0;
  s2+=171.0;
 }
}
if(i0<76.0){
 if(i6<-1.9131298065185547){
  s1+=4.0;
 } else {
  s0+=221.0;
  s1+=8.0;
 }
} else {
 if(i1<161.16342163085938){
  s0+=1.0;
  s1+=90.0;
  s2+=1.0;
 } else {
  s1+=8.0;
  s2+=187.0;
 }
}
if(i0<68.0){
 if(i7<-30.667844772338867){
  s1+=7.0;
 } else {
  s0+=209.0;
  s1+=6.0;
 }
} else {
 if(i2<1.1189117431640625){
  s0+=3.0;
  s1+=47.0;
  s2+=176.0;
 } else {
  s1+=52.0;
  s2+=20.0;
 }
}
if(i2<1.2598915100097656){
 if(i4<144.0){
  s0+=209.0;
  s1+=62.0;
 } else {
  s2+=178.0;
 }
} else {
 if(i4<180.0){
  s1+=53.0;
 } else {
  s1+=3.0;
  s2+=15.0;
 }
}
if(i0<76.0){
 if(i7<-18.817075729370117){
  s0+=20.0;
  s1+=8.0;
 } else {
  s0+=176.0;
  s1+=6.0;
 }
} else {
 if(i6<1.1529541015625){
  s1+=54.0;
  s2+=194.0;
 } else {
  s1+=44.0;
  s2+=18.0;
 }
}
 float sum_s=s0;
 int cls=1;
 sum_s = sum_s + s1;
 sum_s = sum_s + s2;
 WRITE_IMAGE (out, POS_out_INSTANCE(x,y,z,0), s1 / sum_s);
}
