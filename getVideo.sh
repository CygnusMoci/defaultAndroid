
# 抽帧
for f in $(find . -iname "*.mp4"); do
 temp_name=${f%.*}
 ffmpeg -i $f ${temp_name}_%5d.jpg
done

# md5乱序
for f in $(find . -iname "*.jpg"); do
 resultName=$(echo -n $f |openssl md5)
 mv $f $resultName.jpg
done

#顺序命名
i=0
for f in $(find . -iname "*.jpg"); do
 let i+=1 
 mv $f img$i.jpg
done

#合并视频
ffmpeg -f image2 -i ./img%d.jpg output.mp4

mkdir img
for f in $(find . -iname "*.jpg"); do
 mv $f img/$f
done
#删除中间图片
#for f in $(find . -iname "*.jpg"); do
# rm $f
#done
