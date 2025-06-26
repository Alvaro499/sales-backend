from sqlalchemy import Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.orm import relationship, declarative_base
import datetime

Base = declarative_base()

class Product(Base):
    __tablename__ = "products"
    product_id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    description = Column(String)

class User(Base):
    __tablename__ = "users"
    user_id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)

class Order(Base):
    __tablename__ = "orders"
    order_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.user_id"))
    date = Column(DateTime, default=datetime.datetime.utcnow)

    user = relationship("User", back_populates="orders")
    lines = relationship("OrderLine", back_populates="order")

User.orders = relationship("Order", back_populates="user")

class OrderLine(Base):
    __tablename__ = "order_lines"
    id = Column(Integer, primary_key=True, index=True)
    order_id = Column(Integer, ForeignKey("orders.order_id"))
    product_id = Column(Integer, ForeignKey("products.product_id"))

    order = relationship("Order", back_populates="lines")
    product = relationship("Product")

# Pydantic (API)
from pydantic import BaseModel
from typing import List

class Recommendation(BaseModel):
    product_id: int
    name: str
    score: float

class RecommendationResponse(BaseModel):
    recommendations: List[Recommendation]
