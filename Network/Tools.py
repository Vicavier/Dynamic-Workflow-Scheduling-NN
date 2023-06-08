import matplotlib.pyplot as plt
import numpy as np


def draw(a: np.ndarray, b: np.ndarray):
    costs = [point[0] for point in a]
    make_spans = [point[1] for point in a]

    costs2 = [point[0] for point in b]
    make_spans2 = [point[1] for point in b]
    # 绘制帕累托前沿
    plt.plot(make_spans, costs, marker='x', linestyle='-')
    plt.plot(make_spans2, costs2, marker='o', linestyle='-')
    plt.xlabel('Makespan')
    plt.ylabel('Cost')
    plt.title('Pareto Front: Makespan vs. Cost')

    # 设置横坐标和纵坐标的范围
    plt.xlim(0, max(make_spans) + 1)
    plt.ylim(0, max(costs) + 500)

    plt.show()

def receive_large_data(sock, buffer_size=1024):
    data_chunks = []
    while True:
        chunk = sock.recv(buffer_size)
        if not chunk:
            break
        if b'#' in chunk:
            data_chunks.append(chunk.rstrip(b'#'))
            break
        data_chunks.append(chunk)
    return b''.join(data_chunks)
