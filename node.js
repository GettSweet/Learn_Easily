const express = require("express");
const bodyParser = require("body-parser");
const fs = require("fs");

const app = express();
const PORT = 8000;

// Middleware для обработки JSON
app.use(bodyParser.json());

// Путь к JSON-файлу
const usersFile = "users.json";

// Проверка наличия файла
if (!fs.existsSync(usersFile)) {
  fs.writeFileSync(usersFile, JSON.stringify([]));
}

// Чтение пользователей из файла
const getUsers = () => {
  const data = fs.readFileSync(usersFile);
  return JSON.parse(data);
};

// Запись пользователей в файл
const saveUsers = users => {
  fs.writeFileSync(usersFile, JSON.stringify(users, null, 2));
};

// Обработчик для входа
app.post("/api/login", (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ message: "Почта и пароль обязательны" });
  }

  const users = getUsers();
  const user = users.find(u => u.email === email && u.password === password);

  if (user) {
    res.status(200).json({ message: "Успешный вход", user });
  } else {
    res.status(401).json({ message: "Неверная почта или пароль" });
  }
});

// Регистрация пользователя
app.post("/api/register", (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ message: "Почта и пароль обязательны" });
  }

  const users = getUsers();
  if (users.find(u => u.email === email)) {
    return res.status(400).json({ message: "Пользователь уже существует" });
  }

  const newUser = { id: users.length + 1, email, password };
  users.push(newUser);
  saveUsers(users);

  res.status(201).json({ message: "Пользователь зарегистрирован", user: newUser });
});

// Запуск сервера
app.listen(PORT, () => {
  console.log(`Сервер запущен на http://localhost:${PORT}`);
});
