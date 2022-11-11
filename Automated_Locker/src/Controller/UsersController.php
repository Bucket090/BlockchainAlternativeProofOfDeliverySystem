<?php
namespace App\Controller;

use \Cake\ORM\Locator\LocatorAwareTrait;
use \Cake\ORM\Table;
use Cake\ORM\TableRegistry;

class UsersController extends AppController
{
    public function login(){
        $this->request->allowMethod(['get', 'post']);
        $result = $this->Authentication->getResult();
        // regardless of POST or GET, redirect if user is logged in
        if ($this->request->is('post')) {
            if ($result->isValid()) {
                $session = $this->Authentication->getIdentity();
                $user = $session->getOriginalData();
                
                if ($user->Role_ID == 1 || $user->Role_ID == 3) {
                    return $this->redirect('/Dashboard/owner');
                }
                if ($user->Role_ID == 2) {
                    return $this->redirect('/Dashboard/worker');
                }
            }
        }
        // display error if user submitted and authentication failed
        if ($this->request->is('post') && !$result->isValid()) {
            $this->Flash->error(__('Invalid email or password'));
        }
    }
    public function register(){
        if ($this->request->is('post')) {
            $userTable = $this->getTableLocator()->get('users');
            $newUser = $userTable->newEntity($this->request->getData());

             if(empty($_POST['First_Name']) || empty($_POST['Last_Name']) || empty($_POST['Email']) || empty($_POST['Password'])){
                 $this->Flash->error("Fill in missing input box!");
                 return;
             }

            if ($userTable->save($newUser)) {
                $this->Flash->success("User added!");
                return $this->redirect(["action" => "login"]);
            } else {
                $errors = $newUser->getErrors();
                $errorMessage = "";
                foreach ($errors as $errorArray) {
                    $errorMessage .= array_values($errorArray)[0]."<br>";
                }
            }
            $this->Flash->error("$errorMessage", ['escape' => false]);
        }
    }
    
    public function logout()
    {
        $result = $this->Authentication->getResult();
        // regardless of POST or GET, redirect if user is logged in
        if ($result->isValid()) {
            $this->Authentication->logout();
            return $this->redirect(['controller' => 'Users', 'action' => 'login']);
        }
    }

    public function deleteUser($user_Id){
        if(!is_numeric($user_Id)){
            $this->Flash("user Id is null.");
        }
        try {
            $user = $this->getTableLocator()->get("users");
            $deleteUser = $user->find()->where(['users.User_ID ' => $user_Id])->first();

            if ($user->delete($deleteUser)) {
                $this->Flash->success("User has been removed");
            }
        } catch(Exception $e){
            $this->Flash->error("User cannot be deleted: ".$e->getMessage());
        }
        return $this->redirect(["action" => "view"]);
    }

    public function beforeFilter(\Cake\Event\EventInterface $event)
    {
        parent::beforeFilter($event);
        $this->Authentication->addUnauthenticatedActions(['login','register']);
    }

    public function edit($user_Id){
        $userTable = $this->getTableLocator()->get("users");
        $myuser = $userTable->find()->contain(['role'])->where(['users.User_ID' => $user_Id])->toArray();
        
        $this->set("myuser",$myuser);
        self::editUser($user_Id);
    }

    protected function editUser($user_Id){

        if ($this->request->is('post')) {
            $userTable = $this->getTableLocator()->get("users");
            $currentUser = $userTable->get($user_Id);
            
            $currentUser->First_Name = $_POST['First_Name'];
            $currentUser->Last_Name = $_POST['Last_Name'];
            $currentUser->Wallet_Address = $_POST['Wallet_Address'];
            $currentUser->Email = $_POST['Email'];
            if ($userTable->save($currentUser)) {
                // entity saved
                $this->Flash->success("User Updated !!");
                return $this->redirect(["action" => "view"]);
            }
        }
    }

    public function view(){

        $usersTable = $this->getTableLocator()->get("users");
        $allusers = $usersTable->find()->contain(['role'])->toArray();

        $this->set("allusers",$allusers);
    }
}