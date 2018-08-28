import sys
import pandas as pd
import os
def two_intersect(df_left,filename):
	df_right=pd.read_csv(filename)
	df_result=df_left.merge(df_right,how='inner') 
	return df_result

if __name__=='__main__':
	dir_name=sys.argv[1]
	list_file=os.listdir(dir_name)
	if len(list_file)==0:
		raise Exception('no file in the dir')
	else:
		df_result=pd.read_csv(dir_name+"/"+list_file[0])
		length=len(list_file)
		for i in range(1,length):
			df_result=two_intersect(df_result,dir_name+"/"+list_file[i])
		print(df_result)
