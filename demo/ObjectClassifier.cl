/*
OpenCL RandomForestClassifier
classifier_class_name = PixelClassifier
feature_specification = area,min_intensity,mean_intensity,max_intensity,sum_intensity,standard_deviation_intensity,mean_max_distance_to_centroid_ratio,centroid_x,centroid_y,centroid_z,touching_neighbor_count,average_distance_of_touching_neighbors,average_distance_of_n_nearest_neighbors=1,average_distance_of_n_nearest_neighbors=6,average_distance_of_n_nearest_neighbors=10
num_ground_truth_dimensions = 1
num_classes = 2
num_features = 15
max_depth = 2
num_trees = 10
*/
__kernel void predict (IMAGE_in0_TYPE in0, IMAGE_in1_TYPE in1, IMAGE_in2_TYPE in2, IMAGE_in3_TYPE in3, IMAGE_in4_TYPE in4, IMAGE_in5_TYPE in5, IMAGE_in6_TYPE in6, IMAGE_in7_TYPE in7, IMAGE_in8_TYPE in8, IMAGE_in9_TYPE in9, IMAGE_in10_TYPE in10, IMAGE_in11_TYPE in11, IMAGE_in12_TYPE in12, IMAGE_in13_TYPE in13, IMAGE_in14_TYPE in14, IMAGE_out_TYPE out) {
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
 float i8 = READ_IMAGE(in8, sampler, POS_in8_INSTANCE(x,y,z,0)).x;
 float i9 = READ_IMAGE(in9, sampler, POS_in9_INSTANCE(x,y,z,0)).x;
 float i10 = READ_IMAGE(in10, sampler, POS_in10_INSTANCE(x,y,z,0)).x;
 float i11 = READ_IMAGE(in11, sampler, POS_in11_INSTANCE(x,y,z,0)).x;
 float i12 = READ_IMAGE(in12, sampler, POS_in12_INSTANCE(x,y,z,0)).x;
 float i13 = READ_IMAGE(in13, sampler, POS_in13_INSTANCE(x,y,z,0)).x;
 float i14 = READ_IMAGE(in14, sampler, POS_in14_INSTANCE(x,y,z,0)).x;
 float s0=0;
 float s1=0;
if(i0<575.0){
 s1+=5.0;
} else {
 s0+=4.0;
}
if(i12<27.85994529724121){
 s1+=7.0;
} else {
 s0+=2.0;
}
if(i6<1.682234525680542){
 s1+=5.0;
} else {
 s0+=4.0;
}
if(i7<94.6077651977539){
 s1+=2.0;
} else {
 if(i13<28.94241714477539){
  s1+=1.0;
 } else {
  s0+=6.0;
 }
}
if(i8<138.38291931152344){
 s0+=5.0;
} else {
 s1+=4.0;
}
if(i13<38.47130584716797){
 s1+=6.0;
} else {
 s0+=3.0;
}
if(i2<166.49295043945312){
 s1+=5.0;
} else {
 s0+=4.0;
}
if(i3<240.0){
 s1+=6.0;
} else {
 if(i4<98816.0){
  s1+=1.0;
 } else {
  s0+=2.0;
 }
}
if(i2<166.49295043945312){
 s1+=6.0;
} else {
 s0+=3.0;
}
if(i6<1.682234525680542){
 s1+=3.0;
} else {
 s0+=6.0;
}
 float max_s=s0;
 int cls=1;
 if (max_s < s1) {
  max_s = s1;
  cls=2;
 }
 WRITE_IMAGE (out, POS_out_INSTANCE(x,y,z,0), cls);
}
