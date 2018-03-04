import matplotlib.pyplot as plt
import numpy as np

#f = open("/Users/islamamin/Documents/matrix.txt",'r+') #path required as input
#matrix_text = f.read()
#matrix_array = list(matrix_text)
matrix_array = np.loadtxt("/Users/islamamin/Documents/StarterHacks/matrix.txt",dtype='i',delimiter=',')
fig = plt.figure()
ax = fig.add_subplot(111)    
cax = ax.matshow(matrix_array,cmap=plt.cm.gray_r )
plt.show()
