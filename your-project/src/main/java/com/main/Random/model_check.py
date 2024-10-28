import pandas as pd 

df = pd.read_csv('random_opinion.csv')

z = df['opinion']

print('z: \n', z)