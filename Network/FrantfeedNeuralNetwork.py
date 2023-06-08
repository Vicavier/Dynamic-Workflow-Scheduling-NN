import torch
import torch.nn as nn


class SimpleNeuralNetwork(nn.Module):
    def __init__(self, input_size):
        super(SimpleNeuralNetwork, self).__init__()
        self.input_layer = nn.Linear(input_size, 2 * input_size)
        self.output_layer = nn.Linear(2 * input_size, input_size)

    def forward(self, x):
        return self.output_layer(torch.relu(self.input_layer(x)))
