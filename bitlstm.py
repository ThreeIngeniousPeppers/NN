# -*- coding: utf-8 -*-
"""
Created on Sun Oct 28 06:30:12 2018

@author: julien
"""

import urllib
import json
import numpy as np
from sklearn.preprocessing import MinMaxScaler
from keras.models import Sequential
from keras.layers import Dense, LSTM
from sklearn.metrics import mean_squared_error
import math

import matplotlib.pyplot as plt



def create_dataset(dataset, look_back=1):
	dataX, dataY = [], []
	for i in range(len(dataset)-look_back):
		a = dataset[i:(i+look_back), 0]
		dataX.append(a)
		dataY.append(dataset[i + look_back, 0])
	return np.array(dataX), np.array(dataY)

s = urllib.request.urlopen(r"https://min-api.cryptocompare.com/data/histominute?fsym=BTC&tsym=USD&allData")


jsoncontent = json.loads(s.read().decode("utf-8"))

time = [element['time'] for element in jsoncontent['Data']]
close = [element['close'] for element in jsoncontent['Data']]
high = [element['high'] for element in jsoncontent['Data']]
low = [element['low'] for element in jsoncontent['Data']]
openv = [element['open'] for element in jsoncontent['Data']]
volumefrom = [element['volumefrom'] for element in jsoncontent['Data']]
volumeto = [element['volumeto'] for element in jsoncontent['Data']]

dataset = np.array(list(zip(time, close, high, low, openv, volumefrom, volumeto)))
dataset = dataset[:,1].reshape(-1, 1)
scaler = MinMaxScaler(feature_range=(0, 1))
dataset = scaler.fit_transform(dataset)

train_size = int(len(dataset)*2/3)
test_size = len(dataset) - train_size
train, test = dataset[0:train_size, :], dataset[train_size:, :]

look_back = 1
trainX, trainY = create_dataset(train, look_back)
testX, testY = create_dataset(test)
trainX = np.reshape(trainX, (trainX.shape[0], 1, trainX.shape[1]))
testX = np.reshape(testX, (testX.shape[0], 1, testX.shape[1]))


model = Sequential()
model.add(LSTM(4, input_shape=(1, look_back)))
model.add(Dense(1))
model.compile(loss='mean_squared_error', optimizer='adam')
model.fit(trainX, trainY, epochs=30, batch_size=1, verbose=2)

trainPredict = model.predict(trainX)
testPredict = model.predict(testX)


# invert predictions
trainPredict = scaler.inverse_transform(trainPredict)
trainY = scaler.inverse_transform([trainY])
testPredict = scaler.inverse_transform(testPredict)
testY = scaler.inverse_transform([testY])
# calculate root mean squared error
trainScore = math.sqrt(mean_squared_error(trainY[0], trainPredict[:,0]))
print('Train Score: %.2f RMSE' % (trainScore))
testScore = math.sqrt(mean_squared_error(testY[0], testPredict[:,0]))
print('Test Score: %.2f RMSE' % (testScore))

#%%
fig = plt.figure("test")
fig.clf()
ax = fig.add_subplot(111)

trainPredictPlot = np.empty_like(dataset)
trainPredictPlot[:, :] = np.nan
trainPredictPlot[look_back:len(trainPredict)+look_back, :] = trainPredict

testPredictPlot = np.empty_like(dataset)
testPredictPlot[:, :] = np.nan
testPredictPlot[len(trainPredict)+(look_back*2):len(dataset), :] = testPredict

d = scaler.inverse_transform(dataset)
trp = trainPredictPlot.reshape(-1, 1)
tep = testPredictPlot.reshape(-1, 1)

ax.plot(time, d)
ax.plot(time, trp)
ax.plot(time, tep)


with open('pythonout.csv', 'w') as csv:
    csv.write("time,actual,trainPredict,testPredict")
    for i in range(len(time)):
        csv.write("{},{},{},{}\n".format(time[i], d[i], trp[i], tep[i]))