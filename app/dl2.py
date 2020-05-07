import tensorflow as tf 

# creates nodes in a graph
# "construction phase"
x1 = tf.constant(5)
x2 = tf.constant(6)

result = tf.mul(x1, x2)
print(result)