/*
OpenCL RandomForestClassifier
classifier_class_name = PixelClassifier
feature_specification = original gaussian_blur=1 difference_of_gaussian=1 laplace_box_of_gaussian_blur=1
num_ground_truth_dimensions = 2
num_classes = 2
num_features = 4
max_depth = 2
num_trees = 10
*/
__kernel void predict (IMAGE_in0_TYPE in0, IMAGE_in1_TYPE in1, IMAGE_in2_TYPE in2, IMAGE_in3_TYPE in3, IMAGE_out_TYPE out) {
 sampler_t sampler = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;
 const int x = get_global_id(0);
 const int y = get_global_id(1);
 const int z = get_global_id(2);
 float i0 = READ_IMAGE(in0, sampler, POS_in0_INSTANCE(x,y,z,0)).x;
 float i1 = READ_IMAGE(in1, sampler, POS_in1_INSTANCE(x,y,z,0)).x;
 float i2 = READ_IMAGE(in2, sampler, POS_in2_INSTANCE(x,y,z,0)).x;
 float i3 = READ_IMAGE(in3, sampler, POS_in3_INSTANCE(x,y,z,0)).x;
 float s0=0;
 float s1=0;
if(i0<68.0){
 if(i1<66.07906341552734){
  s0+=283.0;
 } else {
  s0+=1.0;
  s1+=1.0;
 }
} else {
 if(i0<84.0){
  s0+=7.0;
  s1+=18.0;
 } else {
  s0+=2.0;
  s1+=737.0;
 }
}
if(i0<76.0){
 if(i0<68.0){
  s0+=290.0;
  s1+=2.0;
 } else {
  s0+=11.0;
  s1+=8.0;
 }
} else {
 if(i2<-0.4346351623535156){
  s0+=9.0;
  s1+=114.0;
 } else {
  s0+=1.0;
  s1+=614.0;
 }
}
if(i1<73.57752990722656){
 if(i0<60.0){
  s0+=293.0;
 } else {
  s0+=20.0;
  s1+=2.0;
 }
} else {
 if(i3<-18.244346618652344){
  s0+=5.0;
  s1+=18.0;
 } else {
  s0+=1.0;
  s1+=710.0;
 }
}
if(i1<77.14783477783203){
 if(i1<66.0493392944336){
  s0+=288.0;
 } else {
  s0+=15.0;
  s1+=5.0;
 }
} else {
 if(i1<102.75416564941406){
  s0+=8.0;
  s1+=26.0;
 } else {
  s1+=707.0;
 }
}
if(i0<76.0){
 if(i0<60.0){
  s0+=273.0;
 } else {
  s0+=29.0;
  s1+=3.0;
 }
} else {
 if(i1<79.37408447265625){
  s0+=2.0;
 } else {
  s0+=4.0;
  s1+=738.0;
 }
}
if(i1<74.56773376464844){
 if(i2<-0.8512287139892578){
  s0+=67.0;
  s1+=5.0;
 } else {
  s0+=252.0;
 }
} else {
 if(i1<102.75416564941406){
  s0+=10.0;
  s1+=24.0;
 } else {
  s1+=691.0;
 }
}
if(i1<74.6195297241211){
 if(i0<60.0){
  s0+=274.0;
 } else {
  s0+=37.0;
  s1+=4.0;
 }
} else {
 if(i1<87.08724212646484){
  s0+=6.0;
  s1+=11.0;
 } else {
  s0+=2.0;
  s1+=715.0;
 }
}
if(i0<76.0){
 if(i1<73.8017807006836){
  s0+=293.0;
  s1+=1.0;
 } else {
  s0+=2.0;
  s1+=6.0;
 }
} else {
 if(i0<108.0){
  s0+=9.0;
  s1+=31.0;
 } else {
  s1+=707.0;
 }
}
if(i1<74.6195297241211){
 if(i1<66.07906341552734){
  s0+=323.0;
 } else {
  s0+=10.0;
  s1+=2.0;
 }
} else {
 if(i0<108.0){
  s0+=14.0;
  s1+=39.0;
 } else {
  s1+=661.0;
 }
}
if(i0<68.0){
 if(i1<66.0493392944336){
  s0+=286.0;
 } else {
  s0+=8.0;
  s1+=2.0;
 }
} else {
 if(i1<73.8017807006836){
  s0+=7.0;
 } else {
  s0+=6.0;
  s1+=740.0;
 }
}
 float max_s=s0;
 int cls=1;
 if (max_s < s1) {
  max_s = s1;
  cls=2;
 }
 WRITE_IMAGE (out, POS_out_INSTANCE(x,y,z,0), cls);
}
