import socket
import numpy as np
from scipy.spatial.distance import cdist
from scipy.optimize import linear_sum_assignment
from Tools import draw, receive_large_data
import FrantfeedNeuralNetwork
import torch
import torch.nn as nn
import torch.optim as optim
from decimal import Decimal
from config import INPUT_SIZE, CRASH_NUM, EPOCH

while True:
    cnt = 0
    global_x_train = []
    global_y_train = []

    while True:
        cnt += 1
        if cnt == CRASH_NUM + 1:
            # out_x = np.array(global_x_train)
            # out_y = np.array(global_x_train)
            # np.savetxt('output_x.txt', out_x, fmt="%d")
            # np.savetxt('output_y.txt', out_y, fmt="%d")
            break
        # -----------------Data Transfer------------------
        # 启动服务端（python）
        server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server_socket.bind(('localhost', 8000))
        server_socket.listen(1)
        print('[INFO]服务器已启动，等待客户端连接...')
        # 等待客户端连接
        client_socket, addr = server_socket.accept()
        print('[INFO]客户端已连接：', addr)
        print("[INFO]等待從java收到數據:")
        # 接收Java服务端的数据
        print("[INFO]接收到Java服务端的回复:")
        recv_data = receive_large_data(client_socket, 1024).decode()
        print("[INFO]接收第n-1个POF")
        recv_data0, recv_data1 = recv_data.split('$')
        data_list = recv_data0.split('\n')
        POF_n_1 = [[x for x in data.split(';')] for data in data_list][:-1]
        print("[INFO]接收第n个POF")
        data_list = recv_data1.split('\n')
        POF_n = [[x for x in data.split(';')] for data in data_list][:-1]

        # -----------------Pairing---------------------
        POF_n_1 = sorted(POF_n_1, key=lambda x: float(x[1]))
        POF_n = sorted(POF_n, key=lambda x: float(x[1]))
        print(f'length A:{len(POF_n_1)}')
        print(f'length B:{len(POF_n)}')
        A = np.array([[float(x) for x in row[:2]] for row in POF_n_1])
        B = np.array([[float(x) for x in row[:2]] for row in POF_n])
        distances = cdist(A, B, metric='euclidean')

        # 应用匈牙利算法找到最佳匹配
        row_indices, col_indices = linear_sum_assignment(distances)

        # 如果 A 的长度大于 B，我们需要筛选出有效的匹配对
        if len(A) > len(B):
            valid_matches = col_indices < len(B)
            row_indices = row_indices[valid_matches]
            col_indices = col_indices[valid_matches]

        # 如果 B 的长度大于 A，我们需要筛选出有效的匹配对
        elif len(B) > len(A):
            valid_matches = row_indices < len(A)
            row_indices = row_indices[valid_matches]
            col_indices = col_indices[valid_matches]

        # 打印最佳匹配
        # for r, c in zip(row_indices, col_indices):
        #     print(f"Best match: A[{r}] = {A[r]} and B[{c}] = {B[c]}")

        paired_POF = list(zip(row_indices, col_indices))
        # print('[INFO]最佳匹配：', paired_POF)
        draw(A, B)

        # -----------------Generate Dataset-----------------
        task2ins_n_1 = [[float(num) for num in s.strip('[]').split(',')] for s in (list(map(lambda x: x[2], POF_n_1)))]
        task2ins_n = [[float(num) for num in s.strip('[]').split(',')] for s in (list(map(lambda x: x[2], POF_n)))]
        x_index = [_[0] for _ in paired_POF]
        y_index = [_[1] for _ in paired_POF]
        y = torch.tensor([task2ins_n[i] for i in y_index])
        global_x_train += [task2ins_n_1[i] for i in x_index]
        global_y_train += [task2ins_n[i] for i in y_index]
        x_train = torch.tensor(global_x_train)
        y_train = torch.tensor(global_y_train)
        # -----------------Neural Network---------------------
        model = FrantfeedNeuralNetwork.SimpleNeuralNetwork(INPUT_SIZE)
        criterion = nn.L1Loss()  # Mean Absolute Error
        optimizer = optim.Adam(model.parameters(), lr=0.01, betas=(0.9, 0.999), eps=1e-08)
        # 定义训练参数

        # 训练循环
        for epoch in range(EPOCH):
            optimizer.zero_grad()  # Zero the gradient buffers
            outputs = model(x_train)  # Forward pass
            loss = criterion(outputs, y_train)  # Compute loss
            loss.backward()  # Backward pass
            optimizer.step()  # Update weights
            print(f"Epoch {epoch + 1}, loss: {loss}")

        print('[INFO]训练完成!')

        with torch.no_grad():
            y_pred = model(y)
        torch.set_printoptions(sci_mode=False)
        # for i in y_pred:
        #     print(f'predict :{i}')
        # for j in y_train:
        #     print(f'actually:{j}')
        # 转换为NumPy数组
        numpy_array = y.numpy()
        numpy_array2 = y_pred.numpy()
        # 保存为.npy文件
        np.save('y_train.npy', numpy_array)
        np.save('y_pred.npy', numpy_array2)

        # 发送数据到Java服务端
        send_data = ''
        for y in y_pred:
            for _ in y:
                send_data += str(int(Decimal(float(_)).quantize(Decimal("1."), rounding = "ROUND_HALF_UP")))
                send_data += ' '
            send_data += '\n'
        client_socket.send(send_data.encode('UTF-8'))
        print('[INFO]数据已发送')
        # print(global_x_train)
        # 关闭连接
        client_socket.recv(1024)
        client_socket.close()
        server_socket.close()
