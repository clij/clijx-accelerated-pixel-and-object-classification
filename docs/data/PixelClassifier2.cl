/*
OpenCL RandomForestClassifier
classifier_class_name = PixelClassifier
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
if(i4<148.0){
 if(i4<44.0){
  s0+=42.0;
  s1+=1.0;
 } else {
  s0+=10.0;
  s1+=99.0;
  s2+=9.0;
 }
} else {
 if(i0<172.0){
  s1+=24.0;
  s2+=35.0;
 } else {
  s1+=25.0;
  s2+=377.0;
 }
}
if(i3<-2.245210647583008){
 if(i3<-20.780445098876953){
  s0+=1.0;
  s1+=3.0;
  s2+=15.0;
 } else {
  s0+=40.0;
  s1+=29.0;
  s2+=51.0;
 }
} else {
 if(i0<172.0){
  s0+=12.0;
  s1+=58.0;
  s2+=45.0;
 } else {
  s1+=22.0;
  s2+=346.0;
 }
}
if(i5<161.1442413330078){
 if(i6<0.8947982788085938){
  s0+=43.0;
  s1+=88.0;
  s2+=4.0;
 } else {
  s1+=28.0;
  s2+=26.0;
 }
} else {
 if(i3<36.495849609375){
  s1+=6.0;
  s2+=391.0;
 } else {
  s1+=13.0;
  s2+=23.0;
 }
}
if(i0<148.0){
 if(i3<-0.4538593292236328){
  s0+=55.0;
  s1+=42.0;
  s2+=2.0;
 } else {
  s0+=5.0;
  s1+=32.0;
  s2+=14.0;
 }
} else {
 if(i3<36.38343811035156){
  s1+=24.0;
  s2+=407.0;
 } else {
  s1+=20.0;
  s2+=21.0;
 }
}
if(i5<152.33502197265625){
 if(i4<52.0){
  s0+=48.0;
  s1+=8.0;
 } else {
  s0+=4.0;
  s1+=84.0;
  s2+=20.0;
 }
} else {
 if(i1<171.09088134765625){
  s1+=15.0;
  s2+=36.0;
 } else {
  s1+=20.0;
  s2+=387.0;
 }
}
if(i1<155.28817749023438){
 if(i5<53.85901641845703){
  s0+=57.0;
  s1+=4.0;
 } else {
  s1+=101.0;
  s2+=13.0;
 }
} else {
 if(i3<27.63427734375){
  s1+=9.0;
  s2+=337.0;
 } else {
  s1+=25.0;
  s2+=76.0;
 }
}
if(i0<148.0){
 if(i4<52.0){
  s0+=55.0;
  s1+=11.0;
 } else {
  s1+=85.0;
  s2+=9.0;
 }
} else {
 if(i1<171.09088134765625){
  s1+=22.0;
  s2+=37.0;
 } else {
  s1+=16.0;
  s2+=387.0;
 }
}
if(i0<148.0){
 if(i7<1.7169322967529297){
  s0+=55.0;
  s1+=57.0;
  s2+=1.0;
 } else {
  s0+=1.0;
  s1+=26.0;
  s2+=12.0;
 }
} else {
 if(i2<2.45855712890625){
  s1+=18.0;
  s2+=395.0;
 } else {
  s1+=21.0;
  s2+=36.0;
 }
}
if(i7<-0.2910575866699219){
 if(i4<140.0){
  s0+=67.0;
  s1+=55.0;
 } else {
  s2+=88.0;
 }
} else {
 if(i4<164.0){
  s0+=2.0;
  s1+=45.0;
  s2+=25.0;
 } else {
  s1+=25.0;
  s2+=315.0;
 }
}
if(i0<164.0){
 if(i7<1.6493988037109375){
  s0+=52.0;
  s1+=76.0;
  s2+=2.0;
 } else {
  s0+=1.0;
  s1+=37.0;
  s2+=27.0;
 }
} else {
 if(i6<2.4521713256835938){
  s1+=13.0;
  s2+=359.0;
 } else {
  s1+=19.0;
  s2+=36.0;
 }
}
 float max_s=s0;
 int cls=1;
 if (max_s < s1) {
  max_s = s1;
  cls=2;
 }
 if (max_s < s2) {
  max_s = s2;
  cls=3;
 }
 WRITE_IMAGE (out, POS_out_INSTANCE(x,y,z,0), cls);
}
