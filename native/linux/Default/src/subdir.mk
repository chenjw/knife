################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
/Users/chenjw/workspace/knife/native/src/check.c \
/Users/chenjw/workspace/knife/native/src/class.c \
/Users/chenjw/workspace/knife/native/src/field.c \
/Users/chenjw/workspace/knife/native/src/reference.c \
/Users/chenjw/workspace/knife/native/src/trace.c \
/Users/chenjw/workspace/knife/native/src/util.c 

OBJS += \
./src/check.o \
./src/class.o \
./src/field.o \
./src/reference.o \
./src/trace.o \
./src/util.o 

C_DEPS += \
./src/check.d \
./src/class.d \
./src/field.d \
./src/reference.d \
./src/trace.d \
./src/util.d 


# Each subdirectory must supply rules for building sources it contributes
src/check.o: /Users/chenjw/workspace/knife/native/src/check.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I"/Users/chenjw/workspace/knife/native/include/linux" -O2 -g -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/class.o: /Users/chenjw/workspace/knife/native/src/class.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I"/Users/chenjw/workspace/knife/native/include/linux" -O2 -g -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/field.o: /Users/chenjw/workspace/knife/native/src/field.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I"/Users/chenjw/workspace/knife/native/include/linux" -O2 -g -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/reference.o: /Users/chenjw/workspace/knife/native/src/reference.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I"/Users/chenjw/workspace/knife/native/include/linux" -O2 -g -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/trace.o: /Users/chenjw/workspace/knife/native/src/trace.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I"/Users/chenjw/workspace/knife/native/include/linux" -O2 -g -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

src/util.o: /Users/chenjw/workspace/knife/native/src/util.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include -I"/Users/chenjw/workspace/knife/native/include/linux" -O2 -g -Wall -c -fmessage-length=0 -fPIC -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


