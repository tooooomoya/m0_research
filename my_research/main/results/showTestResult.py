import pandas as pd
import matplotlib.pyplot as plt

def plot_csv_data(csv_filename, output_filename, plain_filename, i):
    
    result = pd.read_csv(csv_filename)
    result.columns = result.columns.str.strip()  # 列名の前後の空白を削除
    val_name = csv_filename[:-4]

    lam = result['Lambda']
    val = result['val']

    plt.figure(figsize=(8, 6))
    plt.scatter(lam, val, color='red', label='new result')
    plt.plot(lam, val, color='red', linestyle='-', linewidth=1)
    
    plain_result = pd.read_csv(plain_filename)
    plain_result.columns = plain_result.columns.str.strip()
    plain_lam = plain_result['Lambda']
    plain_val = plain_result['val']
    
    plt.scatter(plain_lam, plain_val, color='green', label='plain result', alpha=0.3)
    plt.plot(plain_lam, plain_val, color='green', alpha=0.3)
    plt.axhline(y=1, color='gray', linestyle='--', linewidth=0.8, alpha=0.7, label='y=1')

    
    if(i==1):
        plt.xlabel('lambda')
        plt.ylabel('percent change in ' + val_name + ' : plt[-1]/plt[0] ')
        plt.title('Polarization with NW admin in ' + val_name)
        plt.legend()
    
    if(i==2):
        plt.xlabel('lambda')
        plt.ylabel('percent change in ' + val_name + ' : dis[-1]/dis[0] ')
        plt.title('Disagreement with NW admin in ' + val_name)
        plt.legend()
    if(i==3):
        plt.xlabel('lambda')
        plt.ylabel('final extremist ratio in  ' + val_name + ' : gppls[-1]/gppls[0]')
        plt.title('GroupPolarization with NW admin in ' + val_name)
        plt.legend()
    if(i==4):
        plt.xlabel('lambda')
        plt.ylabel('final satisfaction in ' + val_name + ' : stfs[-1]/stfs[0]')
        plt.title('User Satisfaction with NW admin in ' + val_name)
        plt.legend()
    if(i==5):
        plt.xlabel('lambda')
        plt.ylabel('user diversity change in ' + val_name + ' : udv[-1]/udv[0]')
        plt.title('User Diversity with NW admin in ' + val_name)
        plt.legend()
    if(i==6):
        plt.xlabel('lambda')
        plt.ylabel('community diversity change in ' + val_name + ' : cdv[-1]/cdv[0]')
        plt.title('Community Diversity with NW admin in ' + val_name)
        plt.legend()
    
    plt.savefig(output_filename)
    plt.close()
    
def plot_csv_data_p(csv_filename, output_filename, plain_filename, i):
    
    result = pd.read_csv(csv_filename)
    result.columns = result.columns.str.strip()  # 列名の前後の空白を削除
    val_name = csv_filename[:-4]

    lam = result['Lambda']
    val = result['val']

    plt.figure(figsize=(8, 6))
    plt.scatter(lam, val, color='red', label='new result')
    plt.plot(lam, val, color='red', linestyle='-', linewidth=1)
    
    plain_result = pd.read_csv(plain_filename)
    plain_result.columns = plain_result.columns.str.strip()
    plain_lam = plain_result['Lambda']
    plain_val = plain_result['val']
    
    plt.scatter(plain_lam, plain_val, color='green', label='plain result', alpha=0.3)
    plt.plot(plain_lam, plain_val, color='green', alpha=0.3)
    plt.axhline(y=0.56, color='gray', linestyle='--', linewidth=0.8, alpha=0.7, label='y=0.56')

    
    if(i==1):
        plt.xlabel('lambda')
        plt.ylabel('percent change in ' + val_name + ' : plt[-1] ')
        plt.title('Polarization with NW admin in ' + val_name)
        plt.legend()
    
    plt.savefig(output_filename)
    plt.close()
    
    



plot_csv_data_p('plsTest.csv', 'testimg/plsTest.jpg','plsTest0.csv', 1)
plot_csv_data('disaggTest.csv', 'testimg/disaggTest.jpg','disaggTest0.csv', 2)
plot_csv_data('gpplsTest.csv', 'testimg/gpplsTest.jpg','gpplsTest0.csv', 3)
plot_csv_data('stfsTest.csv', 'testimg/stfsTest.jpg','stfsTest0.csv', 4)
plot_csv_data('udvTest.csv', 'testimg/udvTest.jpg','udvTest0.csv', 5)
plot_csv_data('cdvTest.csv', 'testimg/cdvTest.jpg','cdvTest0.csv', 6)
