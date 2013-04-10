rm -f prf*.tsv
i=0
while read q ; do
i=$((i + 1));
prfout=prf-$i.tsv;
Q=`echo $q| sed "s/ /%20/g"`;
t="http://localhost:25815/prf?query=$Q&ranker=QL&numdocs=10&numterms=5"
curl $t > $prfout;
echo $q:$prfout >> prf.tsv
done < queries.tsv
java -cp src edu.nyu.cs.cs2580.Bhattacharyya prf.tsv qsim.tsv