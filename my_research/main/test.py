import numpy as np

# Number of samples and iterations
num_samples = 550
num_iterations = 10

# Generate variances over 10 iterations
variances = [np.var(np.random.uniform(0, 1, num_samples)) for _ in range(num_iterations)]

# Calculate the mean of the variances
mean_variance = np.mean(variances)
print(mean_variance)
